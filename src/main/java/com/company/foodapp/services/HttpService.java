package com.company.foodapp.services;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.AuthenticationDetails;
import com.company.foodapp.utils.CookieUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import javax.servlet.http.HttpServletRequest;

@Service
public class HttpService {
    private WebClient webClient;
    private PropertiesFileReader propertiesFileReader;
    private CookieUtils cookieUtils;
    private Logger logger;

    @Autowired
    public HttpService(PropertiesFileReader propertiesFileReader, WebClient.Builder webClientBuilder, CookieUtils cookieUtils, Logger logger) {
        this.propertiesFileReader = propertiesFileReader;
        this.webClient = initWebClient(webClientBuilder);
        this.cookieUtils = cookieUtils;
        this.logger = logger;
    }

    private WebClient initWebClient(WebClient.Builder webClientBuilder) {
        var baseUrl = propertiesFileReader.getProperty("APP_URL");
        var webClient = webClientBuilder.baseUrl(baseUrl).build();
        return webClient;
    }

    public AuthenticationDetails getAuthenticationDetails(HttpServletRequest httpServletRequest) {
        String cookie = null;
        AuthenticationDetails authenticationDetailsResponse = null;

        try {
            cookie = cookieUtils.getCookieValue("token", httpServletRequest);

            logger.info("Successfully retrieved the authentication token");
        } catch (Exception exception) {
            logger.info("Could not retrieve the authentication token, user was not logged in");
            return null;
        }

        try {
            authenticationDetailsResponse = webClient
                    .get()
                    .uri("authorization/getCurrentAuthenticationDetails")
                    .cookie("token", cookie)
                    .retrieve()
                    .bodyToMono(AuthenticationDetails.class)
                    .block();

            logger.info("Successfully retrieved authentication details");
        } catch (WebClientException exception) {
            logger.info("Could not retrieve authentication details");
        }

        return authenticationDetailsResponse;
    }
}
