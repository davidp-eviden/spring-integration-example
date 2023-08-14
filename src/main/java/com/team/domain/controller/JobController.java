package com.team.domain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class JobController {

    @PostMapping("/run")
    public ResponseEntity<Void> runJob(){
        //TODO: Call the job here
        return ResponseEntity.ok().build();
    }

}
