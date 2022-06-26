package com.company.foodapp.controllers;

import com.company.foodapp.dto.MessageDetails;
import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.mappers.ClaimsToAuthenticationDetailsMapper;
import com.company.foodapp.mappers.MessageMapper;
import com.company.foodapp.models.Message;
import com.company.foodapp.repositories.MessageRepository;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
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
    private MessageDetailsValidator messageDetailsValidator;
    private Logger logger;
    private CookieUtils cookieUtils;
    private JwtUtils jwtUtils;
    private ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper;

    @Autowired
    public MessageController(MessageRepository messageRepository, MessageMapper messageMapper, MessageDetailsValidator messageDetailsValidator, Logger logger, CookieUtils cookieUtils, JwtUtils jwtUtils, ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.messageDetailsValidator = messageDetailsValidator;
        this.logger = logger;
        this.cookieUtils = cookieUtils;
        this.jwtUtils = jwtUtils;
        this.claimsToAuthenticationDetailsMapper = claimsToAuthenticationDetailsMapper;
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
        var authenticationTokenCookie = cookieUtils.getCookieValue("token", httpServletRequest);

        if (authenticationTokenCookie == null) {
            logger.info("Could not retrieve authentication token");
        } else {
            logger.info("Authentication token was retrieved");
        }

        var authenticationDetailsClaims = jwtUtils.decodeJWT(authenticationTokenCookie);

        if (authenticationDetailsClaims == null) {
            logger.info("Could not decode authentication token");
        } else {
            logger.info("Authentication token was decoded");
        }

        var authenticationDetails = claimsToAuthenticationDetailsMapper.map(authenticationDetailsClaims);

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
