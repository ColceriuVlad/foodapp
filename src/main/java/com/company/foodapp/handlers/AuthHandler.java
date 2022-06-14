package com.company.foodapp.handlers;

import com.company.foodapp.repositories.UserRepository;
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
    private UserRepository userRepository;
    private Logger logger;

    @Autowired
    public AuthHandler(JwtUtils jwtUtils, CookieUtils cookieUtils, UserRepository userRepository, Logger logger) {
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.userRepository = userRepository;
        this.logger = logger;
    }

    @Override
    public Flow handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, String[] flags) throws Exception {
        logger.info(request.getMethod() + " request is executing on" + request.getRequestURI());
        String token = cookieUtils.getCookieValue("token", request);
        Flow flow = null;
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        if (token == null) {
            logger.info("Request token is null");
        }

        var claims = jwtUtils.decodeJWT(token);
        var username = claims.getSubject();
        var role = claims.get("role", String.class);
        var usersFromDb = userRepository.findAll();

        for (var userFromDb : usersFromDb) {
            if (username.equals(userFromDb.username)) {
                flow = Flow.CONTINUE;
                break;
            } else {
                flow = Flow.HALT;
            }
        }

        if (flow.equals(Flow.HALT)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.info("User " + username + " could not be found in the database");
            return flow;
        }


        // If flags exist, the handler should only allow flagged users to continue
        if (flags.length != 0) {
            for (var flag : flags) {
                if (flag.equals(role)) {
                    flow = Flow.CONTINUE;
                    break;
                } else {
                    flow = Flow.HALT;
                }
            }
        }

        if (flow.equals((Flow.HALT))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            logger.info("User is not authorized");
        }

        return flow;
    }
}