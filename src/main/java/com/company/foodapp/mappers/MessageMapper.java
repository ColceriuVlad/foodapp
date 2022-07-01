package com.company.foodapp.mappers;

import com.company.foodapp.dto.MessageDto;
import com.company.foodapp.models.AuthenticationDetails;
import com.company.foodapp.models.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public Message map(MessageDto messageDto, AuthenticationDetails authenticationDetails) {
        var message = new Message();
        message.description = messageDto.description;
        message.name = messageDto.name;

        message.username = authenticationDetails.subject;
        message.role = authenticationDetails.role;
        message.email = authenticationDetails.email;

        return message;
    }

    public Message map(MessageDto messageDto) {
        var message = new Message();
        message.description = messageDto.description;
        message.name = messageDto.name;
        message.email = messageDto.email;

        return message;
    }
}
