package org.st.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.st.listener.ProductListener;
import org.st.model.Product;
import org.st.process.ProductItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    //
    // 1. Inject the auto-configured DataSource (configured via application.properties)
    //
    @Autowired
    private DataSource dataSource;

    //
    // 2. ItemReader<Product>
    //
    @Bean
    public ItemReader<Product> reader() {
        return new FlatFileItemReader<Product>() {{
            setResource(new ClassPathResource("parts.csv"));
            setLineMapper(new DefaultLineMapper<Product>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames("id", "code", "cost");
                }});
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {{
                    setTargetType(Product.class);
                }});
            }});
        }};
    }

    //
    // 3. ItemProcessor<Product,Product>
    //
    @Bean
    public ItemProcessor<Product, Product> processor() {
        return new ProductItemProcessor();
    }

    //
    // 4. ItemWriter<Product> using the injected DataSource
    //
    @Bean
    public ItemWriter<Product> writer() {
        return new JdbcBatchItemWriter<Product>() {{
            setDataSource(dataSource);
            setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
            setSql(
                "INSERT INTO prodtab (pid, pcode, pcost, pdisc, pgst) "
              + "VALUES (:id, :code, :cost, :disc, :gst)"
            );
        }};
    }

    //
    // 5. StepBuilderFactory is auto-injected by Spring Batch
    //
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepA() {
        return stepBuilderFactory
                .get("stepA")
                .<Product, Product>chunk(5)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    //
    // 6. JobBuilderFactory is auto-injected by Spring Batch
    //
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job jobA() {
        return jobBuilderFactory
                .get("jobA")
                .incrementer(new RunIdIncrementer())
                .listener(new ProductListener())
                .start(stepA())
                .build();
    }
}