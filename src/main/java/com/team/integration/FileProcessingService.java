package com.team.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
@EnableIntegration
public class FileProcessingService {

    @Value("${file.directory.input}")
    private String inputDirectory;

    @Value("${file.directory.output}")
    private String outputDirectory;
    public final String FILE_PATTERN = "*.csv";

    private final Transformer transformer;

    public FileProcessingService(Transformer transformer) {
        this.transformer = transformer;
    }

    @Bean
    public IntegrationFlow integrationFlow(){
        return IntegrationFlow.from(fileReading(), spec -> spec.poller(Pollers.fixedDelay(500)))
                .transform(transformer, "transformToUppercase")
                .handle(fileWriting())
                .get();
    }

    @Bean
    public MessageSource<File> fileReading() {
        FileReadingMessageSource sourceReader= new FileReadingMessageSource();
        sourceReader.setDirectory(new File(this.inputDirectory));
        sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
        return sourceReader;
    }

    @Bean
    public MessageHandler fileWriting() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(this.outputDirectory));
        handler.setExpectReply(false);
        return handler;
    }


}
