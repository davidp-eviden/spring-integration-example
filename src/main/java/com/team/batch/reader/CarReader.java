package com.team.batch.reader;

import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

public class CarReader {

    @Value("${file.directory.input}")
    private String inputDirectory;
    @Bean
    public ItemStreamReader<String> fileItemReader() {
        FlatFileItemReader<String> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(this.inputDirectory));
        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }
}
