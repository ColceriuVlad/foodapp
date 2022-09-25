package com.company.foodapp.validators;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SecretsValidator {
    private List<String> secrets;

    public SecretsValidator() {
        this.secrets = getSecrets();
    }

    private List<String> getSecrets() {
        var secrets = new ArrayList<String>();
        secrets.add("password");
        return secrets;
    }

    public void validateMap(Map map) {
        for (var secret : secrets) {
            if (map.containsKey(secret)) {
                map.put(secret, "****");
            }
        }
    }
}
