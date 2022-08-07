package com.company.foodapp.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class CookieUtilsTest {
    private Logger logger;
    private CookieUtils cookieUtils;
    private String cookieName;
    private String cookieValue;
    private HttpServletResponse httpServletResponse;

    public CookieUtilsTest() {
        logger = mock(Logger.class);
        cookieUtils = new CookieUtils(logger);
        cookieName = "cookieName";
        cookieValue = "cookieValue";
        httpServletResponse = mock(HttpServletResponse.class);
    }

    @Test
    public void createCookie() {
        cookieUtils.createCookie(cookieName, cookieValue, httpServletResponse);

        verify(httpServletResponse, times(1)).addCookie(any(Cookie.class));
        verify(httpServletResponse, times(1)).addHeader("Access-Control-Allow-Credentials", "true");
        verify(logger, times(1)).info("Successfully added cookie");
        verify(logger, times(0)).info("Could not add cookie");
    }

    @Test
    public void couldNotCreateCookie() {
        doThrow(Exception.class).when(httpServletResponse).addCookie(any(Cookie.class));

        cookieUtils.createCookie(cookieName, cookieValue, httpServletResponse);

        verify(httpServletResponse, times(1)).addCookie(any(Cookie.class));
        verify(httpServletResponse, times(0)).addHeader("Access-Control-Allow-Credentials", "true");
        verify(logger, times(0)).info("Successfully added cookie");
        verify(logger, times(1)).info("Could not add cookie");
    }

    @Test
    public void getCookieValue() {
        var httpServletRequest = mock(HttpServletRequest.class);

        Cookie[] cookies = {new Cookie(cookieName, cookieValue)};

        when(httpServletRequest.getCookies()).thenReturn(cookies);

        var actualCookieValue = cookieUtils.getCookieValue(cookieName, httpServletRequest);
        Assertions.assertEquals(cookieValue, actualCookieValue);
        verify(logger, times(0)).info("Could not retrieve cookie value, cookie was not found");
    }

    @Test
    public void couldNotGetCookieValue() {
        var httpServletRequest = mock(HttpServletRequest.class);

        Cookie[] cookies = {};

        when(httpServletRequest.getCookies()).thenReturn(cookies);

        var actualCookieValue = cookieUtils.getCookieValue(cookieName, httpServletRequest);
        Assertions.assertEquals(null, actualCookieValue);
        verify(logger, times(1)).info("Could not retrieve cookie value, cookie was not found");
    }
}
