package in.nit.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class MyJobListener 
	implements JobExecutionListener
{

	public void beforeJob(JobExecution je) {
		System.out.println("--started--");
		System.out.println(je.getStatus());
	};
	public void afterJob(JobExecution je) {
		
		System.out.println("--end--");
		System.out.println(je.getStatus());
	}
}





