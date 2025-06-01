package in.nit.reader;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class MyJobListener 
	implements JobExecutionListener
{

	@Override
	public void beforeJob(JobExecution je) {
		System.out.println("--Before Job--");
		System.out.println(je.getStatus());
	}

	@Override
	public void afterJob(JobExecution je) {
		System.out.println("--After Job--");
		System.out.println(je.getStatus());
		
	}

	
}
