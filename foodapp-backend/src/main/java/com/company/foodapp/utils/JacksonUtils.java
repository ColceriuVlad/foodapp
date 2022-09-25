package com.company.foodapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JacksonUtils {
    private ObjectMapper objectMapper;
    private Logger logger;

    @Autowired
    public JacksonUtils(ObjectMapper objectMapper, Logger logger) {
        this.objectMapper = objectMapper;
        this.logger = logger;
    }

    public String parseObjectToJson(Object object) {
        try {
            var json = objectMapper.writeValueAsString(object);
            logger.info("Successfully parsed object to json");

            return json;
        } catch (JsonProcessingException e) {
            logger.info("Could not parse object to json");
            return null;
        }
    }

    public <T> T parseJsonToObject(String json, Class<T> typeOfObject) throws JsonProcessingException {
        return objectMapper.readValue(json, typeOfObject);
    }
}
