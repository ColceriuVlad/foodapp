package com.company.foodapp.utils;

import com.company.foodapp.models.FormattedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class RequestUtils {
    private JacksonUtils jacksonUtils;

    @Autowired
    public RequestUtils(JacksonUtils jacksonUtils) {
        this.jacksonUtils = jacksonUtils;
    }

    public String getFormattedRequestAsString(ContentCachingRequestWrapper requestWrapper) throws IOException {
        var requestURL = requestWrapper.getRequestURI();
        var requestMethodType = requestWrapper.getMethod();
        var requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
        var formattedRequest = new FormattedRequest(requestURL, requestMethodType, requestBody);
        var formattedRequestAsString = jacksonUtils.parseObjectToJson(formattedRequest);
        return formattedRequestAsString;
    }
}
