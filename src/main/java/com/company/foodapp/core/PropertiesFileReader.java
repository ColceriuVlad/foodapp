package com.company.foodapp.core;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class PropertiesFileReader {
    private Properties properties;

    public Properties getProperties(String fileName) {
        properties = new Properties();
        var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
