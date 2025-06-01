package in.nit.runner;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyJobRunner 
	implements CommandLineRunner
{
	//1. Read Job object from Container(AppConfig)
	@Autowired
	private Job job;
	
	//2. Read Job Launcher given by conatiner
	@Autowired
	private JobLauncher jobLauncher; 

	@Override
	public void run(String... args) throws Exception {
		//3. Define Job Parameters
		JobParameters jobParameters=new JobParametersBuilder()
		.addLong("time", System.currentTimeMillis())
		.toJobParameters();
		//4. start job
		jobLauncher.run(job, jobParameters);
		System.out.println("--------------------");
	}
}






