package com.company.foodapp.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StringUtils {
    public String generateValidationCode() {
        var stringBuilder = new StringBuilder();
        var allowedCharacters = "12345678abcdefghijkl";
        var random = new Random();

        for (var i = 0; i < 16; i++) {
            stringBuilder.append(allowedCharacters.charAt(random.nextInt(16)));
        }

        var validationCode = stringBuilder.toString();
        return validationCode;
    }
}
