package com.company.foodapp.filters;


import com.company.foodapp.models.FormattedRequest;
import com.company.foodapp.utils.JacksonUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class RequestLoggingFilter implements Filter {
    private JacksonUtils jacksonUtils;
    private Logger logger;

    @Autowired
    public RequestLoggingFilter(JacksonUtils jacksonUtils, Logger logger) {
        this.jacksonUtils = jacksonUtils;
        this.logger = logger;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpServletRequest = (HttpServletRequest) request;
        var httpServletRequestReader = httpServletRequest.getReader();
        var requestBody = IOUtils.toString(httpServletRequestReader);
        var formattedRequest = new FormattedRequest(((HttpServletRequest) request).getRequestURI(), ((HttpServletRequest) request).getMethod(), requestBody);
        var formattedRequestString = jacksonUtils.parseObjectToJson(formattedRequest);
        logger.info("*** Sending request: " + formattedRequestString + "***");
    }
}
