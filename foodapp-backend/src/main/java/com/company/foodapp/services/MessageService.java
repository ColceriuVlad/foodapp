package com.company.foodapp.services;

import com.company.foodapp.dto.MessageDto;
import com.company.foodapp.exceptions.NotValidatedException;
import com.company.foodapp.mappers.ClaimsToAuthenticationDetailsMapper;
import com.company.foodapp.mappers.MessageMapper;
import com.company.foodapp.models.Message;
import com.company.foodapp.repositories.MessageRepository;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
import com.company.foodapp.validators.MessageDetailsValidator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MessageService {
    private MessageRepository messageRepository;
    private Logger logger;
    private CookieUtils cookieUtils;
    private JwtUtils jwtUtils;
    private ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper;
    private MessageMapper messageMapper;
    private MessageDetailsValidator messageDetailsValidator;

    @Autowired
    public MessageService(MessageRepository messageRepository, Logger logger, CookieUtils cookieUtils, JwtUtils jwtUtils, ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper, MessageMapper messageMapper, MessageDetailsValidator messageDetailsValidator) {
        this.messageRepository = messageRepository;
        this.logger = logger;
        this.cookieUtils = cookieUtils;
        this.jwtUtils = jwtUtils;
        this.claimsToAuthenticationDetailsMapper = claimsToAuthenticationDetailsMapper;
        this.messageMapper = messageMapper;
        this.messageDetailsValidator = messageDetailsValidator;
    }

    public List<Message> getAllMessages() {
        var messageList = messageRepository.findAll();

        if (!messageList.isEmpty()) {
            logger.info("Successfully retrieved the message list");

            return messageList;
        } else {
            throw new EntityNotFoundException("Could not retrieve any message from the application");
        }
    }

    public void insertMessage(MessageDto messageDto, HttpServletRequest httpServletRequest) {
        var authenticationToken = cookieUtils.getCookieValue("token", httpServletRequest);

        if (authenticationToken == null) {
            logger.info("Could not retrieve authentication token");
        } else {
            logger.info("Authentication token was retrieved");
        }

        var authenticationDetailsClaims = jwtUtils.decodeJWT(authenticationToken);

        if (authenticationDetailsClaims == null) {
            logger.info("Could not decode authentication token");
        } else {
            logger.info("Authentication token was decoded");
        }

        var authenticationDetails = claimsToAuthenticationDetailsMapper.map(authenticationDetailsClaims);

        // If user is authenticated, include username and role in the message, otherwise send a message with just messageDetails
        if (authenticationDetails != null) {
            var validatedMessageDto = messageDetailsValidator.getValidatedMessageDto(messageDto);

            if (validatedMessageDto != null) {
                var message = messageMapper.map(messageDto, authenticationDetails);
                messageRepository.save(message);

                logger.info("Message was sent to the database by authenticated user " + authenticationDetails.subject);
            } else {
                throw new NotValidatedException("Could not validate the message details, incorrect request by user" + authenticationDetails.subject);
            }
        } else {
            var validatedMessageDto = messageDetailsValidator.getValidatedMessageDtoWithEmail(messageDto);

            if (validatedMessageDto != null) {
                var message = messageMapper.map(messageDto);
                messageRepository.save(message);

                logger.info("Message was sent to the database");
            } else {
                throw new NotValidatedException("Could not validate the message details, incorrect request by user" + authenticationDetails.subject);
            }
        }
    }
}
