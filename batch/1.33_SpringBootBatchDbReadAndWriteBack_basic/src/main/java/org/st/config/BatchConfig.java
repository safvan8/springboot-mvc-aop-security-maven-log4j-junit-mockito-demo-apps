package org.st.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
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

    // 1) Auto-configured DataSource (from application.properties)
    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcCursorItemReader<Product> reader() {
        return new JdbcCursorItemReaderBuilder<Product>()
            .name("productReader")
            .dataSource(dataSource)
            .sql(
              "SELECT " +
              "  PID   AS id,   " +
              "  PCODE AS code, " +
              "  PCOST AS cost, " +
              "  PDISC AS disc, " +
              "  PGST  AS gst   " +
              "FROM PRODTAB"
            )
            .rowMapper(new BeanPropertyRowMapper<>(Product.class))
            .build();
    }

    // 3) Processor: add ₹10 to cost and ₹10 to gst
    @Bean
    public ItemProcessor<Product, Product> processor() {
        return new ProductItemProcessor();
    }

    // 4) Writer: UPDATE PRODTAB SET PCOST = :cost, PGST = :gst WHERE PID = :id
    @Bean
    public JdbcBatchItemWriter<Product> writer() {
        JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(
            new BeanPropertyItemSqlParameterSourceProvider<>()
        );
        writer.setSql(
            "UPDATE PRODTAB " +
            "   SET PCOST = :cost, " +
            "       PGST  = :gst " +
            " WHERE PID   = :id"
        );
        return writer;
    }

    // 5) Step: chunk of 5, read → process → write
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepUpdatePrices() {
        return stepBuilderFactory
            .get("stepUpdatePrices")
            .<Product, Product>chunk(5)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    // 6) Job: RunIdIncrementer + listener + our single step
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job jobUpdateProductPrices() {
        return jobBuilderFactory
            .get("jobUpdateProductPrices")
            .incrementer(new RunIdIncrementer())
            .listener(new ProductListener())
            .start(stepUpdatePrices())
            .build();
    }
}
