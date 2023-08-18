package com.example.spring.domain.controller;

import com.example.spring.integration.gateway.IntegrationGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
public class IntegrationController {

    public final IntegrationGateway integrationGateway;

    public IntegrationController(IntegrationGateway integrationGateway) {
        this.integrationGateway = integrationGateway;
    }

    @PostMapping("/start")
    public void getMessageFromIntegrationService(){
        integrationGateway.runJobs("");
    }

}
