package com.company.foodapp.validators;

import com.company.foodapp.dto.MessageDto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageDetailsValidator {
    private Logger logger;

    @Autowired
    public MessageDetailsValidator(Logger logger) {
        this.logger = logger;
    }

    public MessageDto getValidatedMessageDto(MessageDto messageDto) {
        if (messageDto.name != null && messageDto.description != null) {
            logger.info("Message was validated");

            return messageDto;
        } else {
            logger.info("Message was not validated");
            return null;
        }
    }

    public MessageDto getValidatedMessageDtoWithEmail(MessageDto messageDto) {
        if (messageDto.name != null && messageDto.description != null && messageDto.email != null) {
            logger.info("Message was validated (with email)");

            return messageDto;
        } else {
            logger.info("Message was not validated (with email");
            return null;
        }
    }
}
