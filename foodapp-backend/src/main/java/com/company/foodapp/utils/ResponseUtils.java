package com.company.foodapp.utils;

import com.company.foodapp.models.FormattedResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ResponseUtils {
    private Logger logger;
    private JacksonUtils jacksonUtils;

    @Autowired
    public ResponseUtils(Logger logger, JacksonUtils jacksonUtils) {
        this.logger = logger;
        this.jacksonUtils = jacksonUtils;
    }

    public void sendJsonResponse(Object responseObject, HttpServletResponse httpServletResponse) {
        try {
            var printWriter = httpServletResponse.getWriter();
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");

            var json = jacksonUtils.parseObjectToJson(responseObject);

            if (json != null) {
                printWriter.print(json);
                printWriter.flush();
                logger.info("Successfully sent json response");
            } else {
                logger.info("Could not send json response");
            }
        } catch (IOException e) {
            logger.info("Could not send json response");
        }
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
