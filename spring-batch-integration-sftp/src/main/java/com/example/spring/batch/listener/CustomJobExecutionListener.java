package com.example.spring.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j // Similar to: private static Logger log = LoggerFactory.getLogger(CustomJobExecutionListener.class);
@Component
public class CustomJobExecutionListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        switch (jobExecution.getStatus()) {
            case STARTED -> log.info("== JOB STARTED ==");
            case STOPPED -> log.info("== JOB STOPPED ==");
            case COMPLETED -> log.info("== JOB COMPLETED ==");
        }
    }
}
