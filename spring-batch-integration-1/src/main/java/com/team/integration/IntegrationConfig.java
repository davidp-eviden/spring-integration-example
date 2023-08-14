package com.team.integration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;

import java.io.File;

@Configuration
@EnableBatchIntegration
public class IntegrationConfig {

    @Value("${file.directory.input}")
    private String inputDirectory;

    @Value("${file.directory.output}")
    private String outputDirectory;
    public final String FILE_PATTERN = "*.csv";

    private final JobLauncher jobLauncher;

    private final Job sampleJob;

    @Autowired
    private JobRepository jobRepository;

    public IntegrationConfig(JobLauncher jobLauncher, Job sampleJob) {
        this.jobLauncher = jobLauncher;
        this.sampleJob = sampleJob;
    }


    @Bean
    public DirectChannel inputChannel(){
        return new DirectChannel();
    }

    @Bean
    public DirectChannel outputChannel(){
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow integrationFlow(JobLaunchingGateway jobLaunchingGateway) {
        return IntegrationFlow.from(fileReading(), spec -> spec.poller(Pollers.fixedDelay(500)))
                .channel(inputChannel())
                .transform(fileMessageToJobRequest()) // convert the data into a job request to be executable
                .handle(jobLaunchingGateway) //TODO: Call the JOB
                .handle(fileWriting())
                .get();
    }

    @Bean
    public MessageSource<File> fileReading() {
        FileReadingMessageSource sourceReader = new FileReadingMessageSource();
        sourceReader.setDirectory(new File(this.inputDirectory));
        sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN)); // Set a filter to accepts .csv files.
        return sourceReader;
    }


    @Bean
    public FileWritingMessageHandler fileWriting(){
        FileWritingMessageHandler messageHandler = new FileWritingMessageHandler(new File(this.outputDirectory));
        messageHandler.setAutoCreateDirectory(true); // If the input directory doesn't exist create a new directory.
        messageHandler.setExpectReply(false);
        messageHandler.setAppendNewLine(true);
        return messageHandler;
    }

    @Bean
    public FileMessageToJobRequest fileMessageToJobRequest(){
        FileMessageToJobRequest  transformerToJobRequest = new FileMessageToJobRequest();
        transformerToJobRequest.setJob(this.sampleJob);
        transformerToJobRequest.setFileParameterName("filePath");
        return transformerToJobRequest;
    }

    @Bean
    public JobLaunchingGateway sampleJobLaunchingGateway() {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(this.jobRepository);
        jobLauncher.setTaskExecutor(new SyncTaskExecutor());
        JobLaunchingGateway jobLaunchingGateway = new JobLaunchingGateway(jobLauncher);
        jobLaunchingGateway.setOutputChannel(outputChannel());

        return jobLaunchingGateway;
    }


    /*
    @ServiceActivator
    public JobExecution launch(JobLaunchRequest request) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = request.getJob();
        JobParameters jobParameters = request.getJobParameters();

        return jobLauncher.run(job,jobParameters);
    }
    */


    /*
    @Bean
    public JobLaunchingMessageHandler jobLaunchingMessageHandler() {
        return new JobLaunchingMessageHandler(this.jobLauncher);
    }

     */






}
