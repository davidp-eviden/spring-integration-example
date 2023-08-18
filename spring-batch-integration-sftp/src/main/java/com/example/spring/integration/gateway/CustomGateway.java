package com.example.spring.integration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.io.File;

//@MessagingGateway
public interface CustomGateway {
    /*
    @Gateway(requestChannel = "toSftpChannel")
    void sendToSftp(File file);
    */
}
