package com.company.foodapp.utils;

import com.company.foodapp.models.FormattedRequest;
import com.company.foodapp.validators.SecretsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Map;

@Component
public class RequestUtils {
    private JacksonUtils jacksonUtils;
    private SecretsValidator secretsValidator;

    @Autowired
    public RequestUtils(JacksonUtils jacksonUtils, SecretsValidator secretsValidator) {
        this.jacksonUtils = jacksonUtils;
        this.secretsValidator = secretsValidator;
    }

    public String getFormattedRequestAsString(ContentCachingRequestWrapper requestWrapper) throws IOException {
        var requestURL = requestWrapper.getRequestURI();
        var requestMethodType = requestWrapper.getMethod();
        var requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
        var requestBodyMap = jacksonUtils.parseJsonToObject(requestBody, Map.class);
        secretsValidator.validateMap(requestBodyMap);
        var formattedRequestBody = jacksonUtils.parseObjectToJson(requestBodyMap);
        var formattedRequest = new FormattedRequest(requestURL, requestMethodType, formattedRequestBody);
        var formattedRequestAsString = jacksonUtils.parseObjectToJson(formattedRequest);
        return formattedRequestAsString;
    }
}
