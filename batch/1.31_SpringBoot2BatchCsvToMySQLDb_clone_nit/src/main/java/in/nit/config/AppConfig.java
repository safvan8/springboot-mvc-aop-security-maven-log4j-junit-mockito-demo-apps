package in.nit.config;


import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
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
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import in.nit.listener.MyJobListener;
import in.nit.model.Product;
import in.nit.process.MyProductProcessor;

@Configuration
@EnableBatchProcessing
public class AppConfig {

	//6. Autowire JBF
	@Autowired
	private JobBuilderFactory jf;
	
	//7. Bean for Listsner
	@Bean
	public JobExecutionListener listener() {
		return new MyJobListener();
	}
	
	
	//8. Job Bean
	@Bean
	public Job jobA() {
		return jf.get("jobA")
				.incrementer(new RunIdIncrementer())
				.listener(listener() )
				.start(stepA())
				//.next(decider)
				.build();
	}
	
	//4. Autowire StepBuilderFactory
	@Autowired
	private StepBuilderFactory sf;
	
	//5. Step 
	@Bean
	public Step stepA() {
		return sf.get("stepA")
				.<Product,Product>chunk(3)
				.reader(reader())
				.processor(process())
				.writer(writer())
				.build()
				;
	}
	
	//1. ItemReader
	@Bean
	public ItemReader<Product> reader(){
		//JDK 1.7 Type Inference
		FlatFileItemReader<Product> reader=new FlatFileItemReader<>();
		
		reader.setResource(new ClassPathResource("product.csv"));
		reader.setLineMapper(new DefaultLineMapper<Product>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames("prodId","prodCode","prodCost");
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {{
				setTargetType(Product.class);
			}});
		}});  
		
		return reader;
	}
	
	//2. ItemProcessor
	@Bean
	public ItemProcessor<Product, Product> process(){
		return new MyProductProcessor();
	}
	//3. ItemWriter
	@Bean
	public ItemWriter<Product> writer(){
		JdbcBatchItemWriter<Product> writer=new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource());
		writer.setSql("INSERT INTO PRODTAB(PID,PCODE,PCOST,PGST,PDISC) VALUES(:prodId,:prodCode,:prodCost,:prodGst,:prodDisc)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
		return writer;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource ds=new DriverManagerDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/nitdb");
		ds.setUsername("root");
		ds.setPassword("root");
		return ds;
	}
	
	
}










