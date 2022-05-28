package com.company.foodapp.utils;

import com.company.foodapp.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JacksonUtils {
    private ObjectMapper objectMapper = getObjectMapper();

    private ObjectMapper getObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    public List<User> parseJsonAsUsersList(String json) {
        List<User> userList;

        try {
            userList = objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            userList = null;
        }

        return userList;
    }
}
