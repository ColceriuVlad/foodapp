package com.company.foodapp.utils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class CookieUtils {
    private Logger logger;

    @Autowired
    public CookieUtils(Logger logger) {
        this.logger = logger;
    }

    public void createCookie(String cookieName, String cookieValue, HttpServletResponse httpServletResponse) {
        var cookie = new Cookie(cookieName, cookieValue);

        try {
            cookie.setPath("/");
            cookie.setSecure(false);
            cookie.setHttpOnly(true);
            httpServletResponse.addCookie(cookie);
            httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");

            logger.info("Successfully added cookie");
        } catch (Exception exception) {
            logger.info("Could not add cookie");
        }
    }

    public String getCookieValue(String cookieName, HttpServletRequest httpServletRequest) {
        var cookies = httpServletRequest.getCookies();
        String cookieValue = null;

        if (cookies.length != 0) {
            for (var cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookieValue = cookie.getValue();
                }
            }
        } else {
            logger.info("Could not retrieve cookie value, cookie was not found");
        }

        return cookieValue;
    }
}
