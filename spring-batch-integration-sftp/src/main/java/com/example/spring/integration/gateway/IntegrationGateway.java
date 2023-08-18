package com.example.spring.integration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway
public interface IntegrationGateway {

    @Gateway(requestChannel = "integration.gateway.channel")
    void runJobs(String msg);
}
