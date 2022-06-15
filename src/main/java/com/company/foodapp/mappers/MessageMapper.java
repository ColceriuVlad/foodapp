package com.company.foodapp.mappers;

import com.company.foodapp.dto.MessageDetails;
import com.company.foodapp.dto.UserDetails;
import com.company.foodapp.models.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public Message map(MessageDetails messageDetails, UserDetails userDetails) {
        var message = new Message();
        message.description = messageDetails.description;
        message.name = messageDetails.name;

        message.username = userDetails.subject;
        message.role = userDetails.role;
        message.email = userDetails.email;

        return message;
    }
}
