package org.st.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;

import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean; // alternative
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.st.listener.ProductListener;
import org.st.model.Product;
import org.st.process.ProductItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    // 1) Inject Spring Boot’s auto-configured DataSource
    @Autowired
    private DataSource dataSource;

    // 2) Reader: JdbcPagingItemReader<Product> with pageSize = 1000
    //
    // JdbcPagingItemReader<Product> that reads 1 000 rows at a time
    //
    @Bean
    public JdbcPagingItemReader<Product> reader() throws Exception {
        // Build the MySQL‐specific paging query provider
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

        // Select exactly your table's columns (lowercase in MySQL), aliasing to
        // match your Product fields (id, code, cost, disc, gst).
        queryProvider.setSelectClause(
            "SELECT " +
            "  pid   AS id,   " +
            "  pcode AS code, " +
            "  pcost AS cost, " +
            "  pdisc AS disc, " +
            "  pgst  AS gst   "
        );
        queryProvider.setFromClause("FROM PRODTAB");

        // Sort by pid ascending (must match the actual column name in PRODTAB)
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<Product>()
            .name("productPagingReader")
            .dataSource(dataSource)
            .queryProvider(queryProvider)
            .pageSize(1000)    // fetch 1 000 rows per page
            .fetchSize(1_000)   // hint to the JDBC driver
            .rowMapper(new BeanPropertyRowMapper<>(Product.class))
            .build();
    }

    // 3) Processor: add ₹10 to cost and ₹10 to gst
    @Bean
    public ProductItemProcessor processor() {
        return new ProductItemProcessor();
    }

    // 4) Writer: JdbcBatchItemWriter<Product> (1000 updates per chunk)
    //
    //  JdbcBatchItemWriter<Product> that issues 1 000 updates at once
    //
    @Bean
    public JdbcBatchItemWriter<Product> writer() {
        return new JdbcBatchItemWriterBuilder<Product>()
            .dataSource(dataSource)
            // Use the correct parameter‐source provider (maps bean properties to “:id”, “:cost”, “:gst”)
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql(
              "UPDATE PRODTAB " +
              "   SET pcost = :cost, " +
              "       pgst  = :gst  " +
              " WHERE pid   = :id"
            )
            .build();
    }

    // 5) Step definition: chunk size = 1000
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepUpdatePrices() throws Exception {
        return stepBuilderFactory
            .get("stepUpdatePrices")
            .<Product, Product>chunk(1_000)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    // 6) Job: RunIdIncrementer + listener + single step
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job jobUpdateProductPrices() throws Exception {
        return jobBuilderFactory
            .get("jobUpdateProductPrices")
            .incrementer(new RunIdIncrementer())
            .listener(new ProductListener())
            .start(stepUpdatePrices())
            .build();
    }
}
