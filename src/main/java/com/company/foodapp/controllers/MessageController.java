package com.company.foodapp.controllers;

import com.company.foodapp.dto.MessageDetails;
import com.company.foodapp.dto.UserDetails;
import com.company.foodapp.mappers.ClaimsToUserDetailsMapper;
import com.company.foodapp.mappers.MessageMapper;
import com.company.foodapp.models.Message;
import com.company.foodapp.repositories.MessageRepository;
import com.company.foodapp.services.HttpService;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
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
    private CookieUtils cookieUtils;
    private JwtUtils jwtUtils;
    private ClaimsToUserDetailsMapper claimsToUserDetailsMapper;
    private MessageMapper messageMapper;
    private HttpService httpService;
    private Logger logger;

    @Autowired
    public MessageController(MessageRepository messageRepository, CookieUtils cookieUtils, JwtUtils jwtUtils, ClaimsToUserDetailsMapper claimsToUserDetailsMapper, MessageMapper messageMapper, HttpService httpService, Logger logger) {
        this.messageRepository = messageRepository;
        this.cookieUtils = cookieUtils;
        this.jwtUtils = jwtUtils;
        this.claimsToUserDetailsMapper = claimsToUserDetailsMapper;
        this.messageMapper = messageMapper;
        this.httpService = httpService;
        this.logger = logger;
    }

    @GetMapping
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
        ResponseEntity response;

        try {
            var userDetails = httpService.getUserDetails(httpServletRequest);
            var message = messageMapper.map(messageDetails, userDetails);
            messageRepository.save(message);

            logger.info("Message was sent successfully");
            response = new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Could not sent message successfully");
            response = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return response;
    }
}
