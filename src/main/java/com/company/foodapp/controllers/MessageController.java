package com.company.foodapp.controllers;

import com.company.foodapp.dto.MessageDetails;
import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.mappers.MessageMapper;
import com.company.foodapp.models.Message;
import com.company.foodapp.repositories.MessageRepository;
import com.company.foodapp.services.HttpService;
import com.company.foodapp.validators.MessageDetailsValidator;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/messages")
@RestController
public class MessageController {
    private MessageRepository messageRepository;
    private MessageMapper messageMapper;
    private HttpService httpService;
    private MessageDetailsValidator messageDetailsValidator;
    private Logger logger;

    @Autowired
    public MessageController(MessageRepository messageRepository, MessageMapper messageMapper, HttpService httpService, MessageDetailsValidator messageDetailsValidator, Logger logger) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.httpService = httpService;
        this.messageDetailsValidator = messageDetailsValidator;
        this.logger = logger;
    }

    @GetMapping
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<List<Message>> getAllMessages() {
        var messageList = messageRepository.findAll();
        ResponseEntity response;

        if (!messageList.isEmpty()) {
            logger.info("Successfully retrieved all the messages from the application");

            response = new ResponseEntity(messageList, HttpStatus.OK);
        } else {
            logger.info("Could not retrieve the messages from the application");

            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @PostMapping
    public ResponseEntity insertMessage(@RequestBody MessageDetails messageDetails, HttpServletRequest httpServletRequest) {
        var authenticationDetails = httpService.getAuthenticationDetails(httpServletRequest);


        // If user is authenticated, include username and role in the message, otherwise send a message with just messageDetails
        if (authenticationDetails != null) {
            var validatedMessageDetails = messageDetailsValidator.getValidatedMessageDetails(messageDetails);

            if (validatedMessageDetails != null) {
                var message = messageMapper.map(messageDetails, authenticationDetails);
                messageRepository.save(message);

                logger.info("Message was sent to the database by authenticated user " + authenticationDetails.subject);
                return new ResponseEntity(HttpStatus.OK);
            } else {
                logger.info("Could not validate the message details, incorrect request by user " + authenticationDetails.subject);
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            var validatedMessageDetails = messageDetailsValidator.getValidatedMessageDetailsWithEmail(messageDetails);

            if (validatedMessageDetails != null) {
                var message = messageMapper.map(messageDetails);
                messageRepository.save(message);

                logger.info("Message was sent to the database");
                return new ResponseEntity(HttpStatus.OK);
            } else {
                logger.info("Could not validate the message details");
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
    }
}
