package com.team.batch.config;


import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class BatchConfig {

    private final Environment enviroment;

    public BatchConfig(Environment enviroment) {
        this.enviroment = enviroment;
    }

    @Bean
    public ItemStreamReader<String> fileItemReader() {
        FlatFileItemReader<String> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(enviroment.getProperty("file.directory.input")));
        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }
}
