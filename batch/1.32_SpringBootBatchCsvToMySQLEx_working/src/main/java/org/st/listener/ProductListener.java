package org.st.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class ProductListener 
	implements JobExecutionListener
{

	@Override
	public void beforeJob(JobExecution je) {
		System.out.println("Before JOB:"+je.getStatus());
	}

	@Override
	public void afterJob(JobExecution je) {
		System.out.println("After JOB:"+je.getStatus());
		
	}
	
	
	
}


