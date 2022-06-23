package com.company.foodapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JacksonUtils {
    private ObjectMapper objectMapper;
    private Logger logger;

    @Autowired
    public JacksonUtils(ObjectMapper objectMapper, Logger logger) {
        this.objectMapper = configureMapper(objectMapper);
        this.logger = logger;
    }

    private ObjectMapper configureMapper(ObjectMapper objectMapper) {
        return objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String parseObjectAsJson(Object object) {
        try {
            var jsonResult = objectMapper.writeValueAsString(object);
            logger.info("Successfully parsed model to json");

            return jsonResult;
        } catch (JsonProcessingException jsonProcessingException) {
            logger.info("Could not parse model to json");
            jsonProcessingException.printStackTrace();

            return null;
        }
    }
}
