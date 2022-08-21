package com.company.foodapp.controllers;

import com.company.foodapp.dto.MessageDto;
import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Message;
import com.company.foodapp.services.MessageService;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/messages")
@RestController
public class MessageController {
    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<List<Message>> getAllMessages() {
        var messageList = messageService.getAllMessages();
        return new ResponseEntity(messageList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity insertMessage(@RequestBody MessageDto messageDto, HttpServletRequest httpServletRequest) {
        messageService.insertMessage(messageDto, httpServletRequest);
        return new ResponseEntity(HttpStatus.OK);
    }
}
