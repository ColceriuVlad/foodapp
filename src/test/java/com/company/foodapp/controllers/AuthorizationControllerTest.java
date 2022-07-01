package com.company.foodapp.controllers;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.models.AuthenticationDetails;
import com.company.foodapp.mappers.ClaimsToAuthenticationDetailsMapper;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.services.EmailService;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
import com.company.foodapp.utils.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationControllerTest {
    private JwtUtils jwtUtils;
    private CookieUtils cookieUtils;
    private ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper;
    private UserRepository userRepository;
    private PropertiesFileReader propertiesFileReader;
    private Logger logger;
    private AuthorizationController authorizationController;
    private EmailService emailService;
    private StringUtils stringUtils;

    public AuthorizationControllerTest() {
        jwtUtils = mock(JwtUtils.class);
        cookieUtils = mock(CookieUtils.class);
        claimsToAuthenticationDetailsMapper = mock(ClaimsToAuthenticationDetailsMapper.class);
        userRepository = mock(UserRepository.class);
        propertiesFileReader = mock(PropertiesFileReader.class);
        logger = mock(Logger.class);
        emailService = mock(EmailService.class);
        stringUtils = mock(StringUtils.class);
        authorizationController = new AuthorizationController(jwtUtils, cookieUtils, claimsToAuthenticationDetailsMapper, userRepository, propertiesFileReader, logger, emailService, stringUtils);
    }

    @Test
    public void login() {
        var servletResponse = mock(HttpServletResponse.class);
        var user = mock(User.class);
        user.username = "username";
        user.password = "password";

        var usersFromDB = new ArrayList<User>();
        usersFromDB.add(new User(1, "username", "password", "admin", "vlad.colceriu@yahoo.com"));

        var expectedToken = "qwerqwerqweofijqwefoij";

        when(propertiesFileReader.getProperty("JWT_AUTHENTICATION_TOKEN_DURATION")).thenReturn("1000");
        when(userRepository.findAll()).thenReturn(usersFromDB);
        when(jwtUtils.createJWT(any(AuthenticationDetails.class))).thenReturn(expectedToken);

        var response = authorizationController.login(user, servletResponse);
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(expectedToken, responseBody);
        Assertions.assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void invalidLogin() {
        var servletResponse = mock(HttpServletResponse.class);
        var user = mock(User.class);
        var usersFromDB = new ArrayList<User>();

        user.username = "username";
        user.password = "password";

        when(userRepository.findAll()).thenReturn(usersFromDB);

        var response = authorizationController.login(user, servletResponse);
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseStatusCode);
    }
}
