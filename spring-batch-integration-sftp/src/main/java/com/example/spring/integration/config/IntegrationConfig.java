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
    //@Qualifier("asyncJobLauncher")
    private final JobLauncher jobLauncher;

    public IntegrationConfig(Job moveToOtherTableAndWriteInCsvJob, JobLauncher jobLauncher) {
        this.job = moveToOtherTableAndWriteInCsvJob;
        this.jobLauncher = jobLauncher;
    }

    // This method is activated when someone subscribes to the launchJobsChannel.
    @ServiceActivator(inputChannel = "launchJobsChannel")
    public Message<?> launchJobs() {
        try {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                    .addDate("date", new Date());
            JobExecution jobExecution  = this.jobLauncher.run(this.job, jobParametersBuilder.toJobParameters());

            // Si el trabajo se ejecuta correctamente, se devuelve una respuesta exitosa.
            return MessageBuilder
                    .withPayload(String.format("The job was launched successfully with the following details: \n %s", jobExecution.toString()))
                    .build();
        } catch (Exception e) {
            // Si se produce una excepción al ejecutar el trabajo, manejarla aquí.
            // Puedes devolver un mensaje de error o cualquier respuesta apropiada.
            return MessageBuilder.withPayload("Error launching the job with the following error: " + e.getMessage()).build();
        }
    }

    @Bean
    public IntegrationFlow inbound() {
        return IntegrationFlow.from(Http.inboundGateway("/launch")
                        .requestMapping(m -> m.methods(HttpMethod.POST)).replyTimeout(10000))
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
    public SessionFactory<SftpClient.DirEntry> sftpSessionFactory(SftpConfig config){
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
    @ServiceActivator(inputChannel = "toSftpChannel") // When the sendToSftp() function is called this channel will be activated.
    public MessageHandler handler (SftpConfig sftpConfig){
        SftpMessageHandler sftpMessageHandler = new SftpMessageHandler(sftpSessionFactory(sftpConfig));
        // Specify the remote directory to send files
        sftpMessageHandler.setRemoteDirectoryExpression(new LiteralExpression(sftpConfig.getDirectory()));
        return sftpMessageHandler;
    }
}
