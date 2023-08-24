package com.example.spring.integration.config;

import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.util.Date;
import java.util.List;

@Configuration
@EnableIntegration
public class IntegrationConfig {
    private final Job job;
    private final JobLauncher jobLauncher;

    public IntegrationConfig(Job moveToOtherTableAndWriteInCsvJob, @Qualifier("asyncJobLauncher") JobLauncher jobLauncher) {
        this.job = moveToOtherTableAndWriteInCsvJob;
        this.jobLauncher = jobLauncher;
    }

    // This method is activated when someone subscribes to the launchJobsChannel.
    @ServiceActivator(inputChannel = "launchJobsChannel")
    public ExitStatus launchJobs() throws JobExecutionException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addDate("date", new Date());
        JobExecution jobExecution = this.jobLauncher.run(this.job, jobParametersBuilder.toJobParameters());
        switch (jobExecution.getStatus()) {

            case COMPLETED: {

                return jobExecution.getExitStatus().addExitDescription("Completed Job").replaceExitCode("200");
            }

            case FAILED: {

                return jobExecution.getExitStatus().addExitDescription("Error").replaceExitCode("500");
            }

            case STOPPED: {

                return jobExecution.getExitStatus().addExitDescription("Stop").replaceExitCode("0");
            }

            case UNKNOWN: {

                return jobExecution.getExitStatus().addExitDescription("Unknown").replaceExitCode("404");
            }

            case STARTING: {

                return jobExecution.getExitStatus().addExitDescription("Running successfully").replaceExitCode("Starting");
            }

            default: {

                return jobExecution.getExitStatus();
            }
        }
    }


    @Bean
    public IntegrationFlow inbound() {
        return IntegrationFlow.from(Http.inboundGateway("/launch")
                        .requestMapping(m -> m.methods(HttpMethod.POST)))
                .channel("launchJobsChannel") // Subscription to the launchJobsChannel
                .get();
    }

    /*
    @Bean
    public IntegrationFlow outbound(){
        //TODO: Manage the response
        return IntegrationFlow.from("launchJobsChannel")
                .handle(Http.outboundGateway("http://localhost:8080/launch")
                        .expectedResponseType(String.class)
                        .httpMethod(HttpMethod.POST))
                .get();
    }

     */

    @Bean
    public SessionFactory<SftpClient.DirEntry> sftpSessionFactory(SftpConfig config) {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(config.getHost());
        factory.setPort(config.getPort());
        factory.setUser(config.getUser());
        factory.setPassword(config.getPassword());
        factory.setAllowUnknownKeys(true); // Allow the connection to unknown hosts
        //Resource privateKeyResource = new DefaultResourceLoader().getResource(config.getPrivateKey());
        //factory.setPrivateKey(privateKeyResource);
        return new CachingSessionFactory<>(factory);
    }

    @Bean
    @ServiceActivator(inputChannel = "toSftpChannel")
    // When the sendToSftp() function is called this channel will be activated.
    public MessageHandler handler(SftpConfig sftpConfig) {
        SftpMessageHandler sftpMessageHandler = new SftpMessageHandler(sftpSessionFactory(sftpConfig));
        // Specify the remote directory to send files
        sftpMessageHandler.setRemoteDirectoryExpression(new LiteralExpression(sftpConfig.getDirectory()));
        return sftpMessageHandler;
    }
}
