package in.nit.config;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.nit.process.MyDataProcessor;
import in.nit.reader.MyDataReader;
import in.nit.reader.MyJobListener;
import in.nit.writer.MyDataWriter;

@Configuration
//9. Activate Batch Processing
@EnableBatchProcessing
public class AppConfig {
	
	//7. Autowire Job Builder Factory
	@Autowired
	private JobBuilderFactory jf;
	
	//4. Autowire StepBuilder Factory
	@Autowired
	private StepBuilderFactory sf;
	
	
	//8. creating one Job ***
	@Bean
	public Job jobA() {
		return jf.get("jobA") //8.1 Job Name
				.incrementer(new RunIdIncrementer()) //8.2 Incrementer
				.listener(listener()) //8.3 Listener
				.start(stepA()) //8.4 First Step
				//.next(stepB()) //8.5 Next Step in order
				.build()  //8.6 Create Job Impl object
				;
	}
	
	
	
	//6. Create object to Job Execution Listener
	@Bean
	public JobExecutionListener listener() {
		return new MyJobListener();
	}
	
	
	
	//5. Step object ****
	@Bean
	public Step stepA() {
		
		return sf.get("stepA")  //5.1 name of step
				.<String,String>chunk(3) //5.2 Input,Output and Chunk size
				.reader(reader()) //5.3 link reader to Step
				.processor(process()) //5.4 link processor to Step
				.writer(writer()) //5.5 link writer to Step
				.build() // return StepImpl cls obj
				;
	}
	

	//3. Item Writer object
	@Bean
	public ItemWriter<String> writer(){
		return new MyDataWriter();
	}
	//2. Item Processor Object
	@Bean
	public ItemProcessor<String, String> process(){
		return new MyDataProcessor();
	}
	//1. Item Reader object
	@Bean
	public ItemReader<String> reader(){
		return new MyDataReader();
	}
	
}





