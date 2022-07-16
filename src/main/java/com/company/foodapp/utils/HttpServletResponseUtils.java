package com.company.foodapp.utils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HttpServletResponseUtils {
    private Logger logger;
    private JacksonUtils jacksonUtils;

    @Autowired
    public HttpServletResponseUtils(Logger logger, JacksonUtils jacksonUtils) {
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
}
