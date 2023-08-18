package com.example.spring.domain.controller;

import com.example.spring.integration.gateway.CustomGateway;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
public class IntegrationController {
    public final CustomGateway customGateway;
    public IntegrationController(CustomGateway customGateway) {
        this.customGateway = customGateway;
    }

    @PostMapping("/start")
    public void launchTheJobs(){
        this.customGateway.runJobs("");
    }
}
