package com.team.integration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

import java.io.File;
import java.util.Date;

@Getter
@Setter
public class FileMessageToJobRequest {
    private Job job;
    private String fileParameterName;
    @Transformer
    public JobLaunchRequest toJobRequest(Message<File> message){
        JobParametersBuilder jobBuilder = new JobParametersBuilder()
                .addDate("date", new Date())
                .addString(this.fileParameterName, message.getPayload().getAbsolutePath());

        return new JobLaunchRequest(this.job, jobBuilder.toJobParameters());

    }
}
