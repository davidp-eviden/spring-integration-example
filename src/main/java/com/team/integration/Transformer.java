package com.team.integration;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class Transformer {
    public String transformToUppercase(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath))).toUpperCase();
    }
}
