package com.company.foodapp.utils;

import com.company.foodapp.models.FormattedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class ResponseUtils {
    private JacksonUtils jacksonUtils;

    @Autowired
    public ResponseUtils(JacksonUtils jacksonUtils) {
        this.jacksonUtils = jacksonUtils;
    }

    public String getFormattedResponseAsString(ContentCachingResponseWrapper responseWrapper) throws IOException {
        var responseStatusCode = responseWrapper.getStatus();
        var responseContent = responseWrapper.getContentAsByteArray();
        var responseCharacterEncoding = responseWrapper.getCharacterEncoding();
        var responseBody = new String(responseContent, responseCharacterEncoding);
        var formattedResponse = new FormattedResponse(responseStatusCode, responseBody);
        var formattedResponseAsString = jacksonUtils.parseObjectToJson(formattedResponse);
        return formattedResponseAsString;
    }
}
