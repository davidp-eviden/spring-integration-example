package com.example.spring.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j // Similar to private static Logger logger = LoggerFactory.getLogger(JobListener.class);
@Component
public class JobListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        switch (jobExecution.getStatus()) {
            case STARTED -> log.info("Job started");
            case STOPPED -> log.info("Job Stopped");
            case COMPLETED -> log.info("Job Completed");
        }
    }
}
