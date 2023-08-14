package com.team.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;


@Component
public class JobListener implements JobExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(JobListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        switch (jobExecution.getStatus()) {
            case FAILED -> logger.info("Job failed");
            case COMPLETED -> logger.info("Job completed");
            case STOPPING -> logger.info("Job stopped");
        }
    }
}

