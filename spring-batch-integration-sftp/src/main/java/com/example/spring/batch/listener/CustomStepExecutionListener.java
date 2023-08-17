package com.example.spring.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j // Similar to: private static Logger log = LoggerFactory.getLogger(CustomStepExecutionListener.class);
@Component
public class CustomStepExecutionListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("before step: " + stepExecution);
        StepExecutionListener.super.beforeStep(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("after step: " + stepExecution);
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
