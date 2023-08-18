package com.example.spring.integration.config;

import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;

import java.util.Collections;
import java.util.Date;

@Configuration
@EnableIntegration
public class IntegrationConfig {
    private final Job job;
    private final JobLauncher jobLauncher;

    public IntegrationConfig(Job moveToOtherTableAndWriteInCsvJob, JobLauncher jobLauncher) {
        this.job = moveToOtherTableAndWriteInCsvJob;
        this.jobLauncher = jobLauncher;
    }

    // This method is activated when someone subscribes to the launchJobsChannel.
    @ServiceActivator(inputChannel = "launchJobsChannel")
    public void launchJobs() throws JobExecutionException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addDate("date", new Date());
        this.jobLauncher.run(job, jobParametersBuilder.toJobParameters());
    }

    @Bean
    public IntegrationFlow inbound() {
        return IntegrationFlow.from(Http.inboundGateway("/launch")
                        .requestMapping(m -> m.methods(HttpMethod.POST))
                        .statusCodeExpression("200") // Set the status code to 200
                        .replyTimeout(300) // The program only has 300 ms to reply.
                ) // The time this method has to solve the reply
                .channel("launchJobsChannel")
                .get();
    }

    @Bean
    public IntegrationFlow outbound(){
        return IntegrationFlow.from("launchJobsChannel")
                .handle(Http.outboundGateway("/launch")
                        .httpMethod(HttpMethod.POST))
                .get();
    }

    /*
        MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
        MessageBuilder.fromMessage(message);
        Message<String> newMessage = MessageBuilder
                .withPayload("Welcome " + message.getPayload() + " to Integration").build();
            replyChannel.send(message);
    */

    /*
    @Bean
    public SessionFactory<SftpClient.DirEntry> sftpSessionFactory(SftpConfig config){
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(config.getHost());
        factory.setPort(config.getPort());
        factory.setUser(config.getUser());
        factory.setPassword(config.getPassword());
        Resource privateKeyResource = new DefaultResourceLoader().getResource(config.getPrivateKey());
        factory.setPrivateKey(privateKeyResource);

        return new CachingSessionFactory<>(factory);
    }

    @Bean
    @ServiceActivator(inputChannel = "toSftpChannel") // When the method sendToSftp() is called this channel will be activated.
    public MessageHandler handler (SftpConfig sftpConfig){
        SftpMessageHandler sftpMessageHandler = new SftpMessageHandler(sftpSessionFactory(sftpConfig));
        sftpMessageHandler.setRemoteDirectoryExpression(new LiteralExpression(sftpConfig.getDirectory()));
        return sftpMessageHandler;
    }
    */
}
