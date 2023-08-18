package com.example.spring.integration.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "application.sftp")
public class SftpConfig {
    private String host;
    private int port;
    private String user;
    private String password;
    // private String privateKey;
    private String directory;
}
