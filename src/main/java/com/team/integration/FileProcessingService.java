package com.team.integration;

import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;

import java.io.File;

@EnableIntegration
public class FileProcessingService {
    private final String INPUT_DIR = "source_dir";
    private final String OUPUT_DIR = "dest_dir";
    public final String FILE_PATTERN = ".txt";


    public MessageSource<File> fileReadingMessageSource(){
        return null;
    }
}
