package org.st.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * Simple listener that prints before/after job.
 */
public class ProductListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println(">>> [ProductListener] Before Job. Status=" 
            + jobExecution.getStatus());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println(">>> [ProductListener] After Job. Status=" 
            + jobExecution.getStatus());
    }
}