package com.company.foodapp.filters;


import com.company.foodapp.utils.RequestUtils;
import com.company.foodapp.utils.ResponseUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingFilter implements Filter {
    private RequestUtils requestUtils;
    private ResponseUtils responseUtils;
    private Logger logger;

    @Autowired
    public LoggingFilter(RequestUtils requestUtils, ResponseUtils responseUtils, Logger logger) {
        this.requestUtils = requestUtils;
        this.responseUtils = responseUtils;
        this.logger = logger;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var httpServletRequest = (HttpServletRequest) servletRequest;
        var httpServletRequestWrapper = new ContentCachingRequestWrapper(httpServletRequest);

        var httpServletResponse = (HttpServletResponse) servletResponse;
        var httpServletResponseWrapper = new ContentCachingResponseWrapper(httpServletResponse);

        filterChain.doFilter(httpServletRequestWrapper, httpServletResponseWrapper);

        var formattedRequestString = requestUtils.getFormattedRequestAsString(httpServletRequestWrapper);
        logger.info("*** Request: " + formattedRequestString + "***");

        var formattedResponseString = responseUtils.getFormattedResponseAsString(httpServletResponseWrapper);
        logger.info("*** Response: " + formattedResponseString + "***");

        httpServletResponseWrapper.copyBodyToResponse();
    }
}
