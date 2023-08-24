package com.example.spring.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * https://docs.spring.io/spring-batch/docs/current/reference/html/job.html#interceptingJobExecution
 */
public class ContractJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution){
    //    jobExecution.
    }

    @Override
    public void afterJob(JobExecution jobExecution){

    }
}
