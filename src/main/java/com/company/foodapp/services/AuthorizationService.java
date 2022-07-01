package com.company.foodapp.services;

import com.company.foodapp.utils.CookieUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class AuthorizationService {
    private CookieUtils cookieUtils;
    private Logger logger;

    @Autowired
    public AuthorizationService(CookieUtils cookieUtils, Logger logger) {
        this.cookieUtils = cookieUtils;
        this.logger = logger;
    }

    public boolean logOut(HttpServletResponse httpServletResponse) {
        try {
            cookieUtils.deleteCookie("token", httpServletResponse);
            logger.info("Successfully logged out");
            return true;
        } catch (Exception exception) {
            logger.info("Could not log out");
            return false;
        }
    }
}
