package com.company.foodapp.services;

import com.company.foodapp.mappers.ClaimsToAuthenticationDetailsMapper;
import com.company.foodapp.models.AuthenticationDetails;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthorizationService {
    private CookieUtils cookieUtils;
    private Logger logger;
    private JwtUtils jwtUtils;
    private ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper;

    @Autowired
    public AuthorizationService(CookieUtils cookieUtils, Logger logger, JwtUtils jwtUtils, ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper) {
        this.cookieUtils = cookieUtils;
        this.logger = logger;
        this.jwtUtils = jwtUtils;
        this.claimsToAuthenticationDetailsMapper = claimsToAuthenticationDetailsMapper;
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

    public AuthenticationDetails getCurrentAuthenticationDetails(HttpServletRequest httpServletRequest) {
        var authenticationToken = cookieUtils.getCookieValue("token", httpServletRequest);

        if (authenticationToken != null) {
            logger.info("Successfully retrieved authentication token");
            var authenticationTokenClaims = jwtUtils.decodeJWT(authenticationToken);

            if (authenticationTokenClaims != null) {
                logger.info("Successfully extracted claims from the authentication token");

                var authenticationDetails = claimsToAuthenticationDetailsMapper.map(authenticationTokenClaims);
                return authenticationDetails;
            } else {
                logger.info("Could not extract the claims from the authentication token");
                return null;
            }
        } else {
            logger.info("Could not retrieve authentication token");
            return null;
        }
    }
}
