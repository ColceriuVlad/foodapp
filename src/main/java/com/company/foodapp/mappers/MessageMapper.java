package com.company.foodapp.mappers;

import com.company.foodapp.dto.MessageDetails;
import com.company.foodapp.dto.AuthenticationDetails;
import com.company.foodapp.models.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public Message map(MessageDetails messageDetails, AuthenticationDetails authenticationDetails) {
        var message = new Message();
        message.description = messageDetails.description;
        message.name = messageDetails.name;

        message.username = authenticationDetails.subject;
        message.role = authenticationDetails.role;
        message.email = authenticationDetails.email;

        return message;
    }

    public Message map(MessageDetails messageDetails) {
        var message = new Message();
        message.description = messageDetails.description;
        message.name = messageDetails.name;
        message.email = messageDetails.email;

        return message;
    }
}
