package com.company.foodapp.handlers;

import com.company.foodapp.models.ErrorResponse;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.DateUtils;
import com.company.foodapp.utils.HttpServletResponseUtils;
import com.company.foodapp.utils.JwtUtils;
import com.kastkode.springsandwich.filter.api.BeforeHandler;
import com.kastkode.springsandwich.filter.api.Flow;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthHandler implements BeforeHandler {
    private JwtUtils jwtUtils;
    private CookieUtils cookieUtils;
    private UserRepository userRepository;
    private Logger logger;
    private DateUtils dateUtils;
    private HttpServletResponseUtils httpServletResponseUtils;

    @Autowired
    public AuthHandler(JwtUtils jwtUtils, CookieUtils cookieUtils, UserRepository userRepository, Logger logger, DateUtils dateUtils, HttpServletResponseUtils httpServletResponseUtils) {
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.userRepository = userRepository;
        this.logger = logger;
        this.dateUtils = dateUtils;
        this.httpServletResponseUtils = httpServletResponseUtils;
    }

    @Override
    public Flow handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, String[] flags) throws Exception {
        logger.info(request.getMethod() + " request is executing on " + request.getRequestURI());
        String token = cookieUtils.getCookieValue("token", request);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        if (token == null) {
            var errorMessage = "Request token is null";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), errorMessage, dateUtils.getCurrentDate());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponseUtils.sendJsonResponse(errorResponse, response);

            return Flow.HALT;
        }

        var claims = jwtUtils.decodeJWT(token);

        if (claims == null) {
            var errorMessage = "Could not decode authentication token";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), errorMessage, dateUtils.getCurrentDate());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponseUtils.sendJsonResponse(errorResponse, response);

            return Flow.HALT;
        }

        var role = claims.get("role", String.class);

        // If flags exist, the handler should only allow flagged users to continue
        if (flags.length != 0) {
            for (var flag : flags) {
                if (flag.equals(role)) {
                    logger.info("Allowing user with role " + role + " to call the application endpoint");
                    return Flow.CONTINUE;
                }
            }
            var errorMessage = "User does not have the corresponding role to call this application endpoint";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), errorMessage, dateUtils.getCurrentDate());
            response.setStatus(HttpStatus.FORBIDDEN.value());

            httpServletResponseUtils.sendJsonResponse(errorResponse, response);

            return Flow.HALT;
        } else {
            logger.info("Allowing the user to call the application endpoint");

            return Flow.CONTINUE;
        }
    }
}