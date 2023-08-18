package com.example.spring.integration.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class IntegrationService {

    private final Job moveToOtherTableAndWriteInCsvJob;

    private final JobLauncher jobLauncher;

    public IntegrationService(Job moveToOtherTableAndWriteInCsvJob, JobLauncher jobLauncher) {
        this.moveToOtherTableAndWriteInCsvJob = moveToOtherTableAndWriteInCsvJob;
        this.jobLauncher = jobLauncher;
    }


    @ServiceActivator(inputChannel = "integration.gateway.channel")
    public void launchJobs() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(moveToOtherTableAndWriteInCsvJob, new JobParameters());
    }
}

        /*
        MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
        MessageBuilder.fromMessage(message);
        Message<String> newMessage = MessageBuilder
                .withPayload("Welcome " + message.getPayload() + " to Integration").build();
            replyChannel.send(message);
        */