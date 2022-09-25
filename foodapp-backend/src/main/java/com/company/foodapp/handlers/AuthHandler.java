package com.company.foodapp.handlers;

import com.company.foodapp.exceptions.NotAuthenticatedException;
import com.company.foodapp.exceptions.NotAuthorizedException;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
import com.kastkode.springsandwich.filter.api.BeforeHandler;
import com.kastkode.springsandwich.filter.api.Flow;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthHandler implements BeforeHandler {
    private JwtUtils jwtUtils;
    private CookieUtils cookieUtils;
    private Logger logger;

    @Autowired
    public AuthHandler(JwtUtils jwtUtils, CookieUtils cookieUtils, Logger logger) {
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.logger = logger;
    }

    @Override
    public Flow handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, String[] flags) throws Exception {
        logger.info(request.getMethod() + " request is executing on " + request.getRequestURI());
        String token = cookieUtils.getCookieValue("token", request);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        if (token == null) {
            throw new NotAuthenticatedException("User needs to be logged in to perform this operation");
        }

        var claims = jwtUtils.decodeJWT(token);

        if (claims == null) {
            throw new NotAuthenticatedException("Authentication token has expired or is incorrect");
        }

        var role = claims.get("role", String.class);

        // If flags exist, the handler should only allow flagged users to continue
        if (flags.length != 0) {
            for (var flag : flags) {
                if (flag.equals(role)) {
                    logger.info("Allowing user with the corresponding role to perform the operation");
                    return Flow.CONTINUE;
                }
            }
            throw new NotAuthorizedException("User is not authorized to perform this operation");
        } else {
            logger.info("Allowing the user to perform the operation");

            return Flow.CONTINUE;
        }
    }
}