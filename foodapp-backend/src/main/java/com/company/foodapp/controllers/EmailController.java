package com.company.foodapp.controllers;

import com.company.foodapp.models.Email;
import com.company.foodapp.services.EmailService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("email")
@RestController
public class EmailController {
    private EmailService emailService;
    private Logger logger;

    @Autowired
    public EmailController(EmailService emailService, Logger logger) {
        this.emailService = emailService;
        this.logger = logger;
    }

    @PostMapping
    public ResponseEntity sendEmail(@RequestBody Email email) {
        try {
            emailService.sendMessage(email);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
