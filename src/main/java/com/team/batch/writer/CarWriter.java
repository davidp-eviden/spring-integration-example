package com.team.batch.writer;

import org.springframework.beans.factory.annotation.Value;

public class CarWriter {

    @Value("${file.directory.output}")
    private String outputDirectory;
}
