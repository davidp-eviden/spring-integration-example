package com.example.spring.integration.config;

import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;

@Configuration
public class IntegrationConfig {

    //TODO: Implement the http integration



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
