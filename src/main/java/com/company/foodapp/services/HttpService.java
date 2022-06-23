package com.company.foodapp.services;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.AuthenticationDetails;
import com.company.foodapp.models.Email;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JacksonUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import javax.servlet.http.HttpServletRequest;

@Service
public class HttpService {
    private WebClient webClient;
    private PropertiesFileReader propertiesFileReader;
    private CookieUtils cookieUtils;
    private Logger logger;
    private JacksonUtils jacksonUtils;

    @Autowired
    public HttpService(PropertiesFileReader propertiesFileReader, WebClient.Builder webClientBuilder, CookieUtils cookieUtils, Logger logger, JacksonUtils jacksonUtils) {
        this.propertiesFileReader = propertiesFileReader;
        this.webClient = initWebClient(webClientBuilder);
        this.cookieUtils = cookieUtils;
        this.logger = logger;
        this.jacksonUtils = jacksonUtils;
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
            var authenticationDetailsEndpoint = propertiesFileReader.getProperty("GET_AUTHENTICATION_DETAILS_ENDPOINT");

            authenticationDetailsResponse = webClient
                    .get()
                    .uri(authenticationDetailsEndpoint)
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

    public HttpStatus sendEmailAndGetStatus(Email email) {
        var sendEmailEndpoint = propertiesFileReader.getProperty("SEND_EMAIL_ENDPOINT");

        var body = jacksonUtils.parseObjectAsJson(email);

        try {
            var sendEmailResponse = webClient
                    .post()
                    .uri(sendEmailEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            var sendEmailResponseStatusCode = sendEmailResponse.getStatusCode();

            logger.info("Request to send email was successfully executed");
            return sendEmailResponseStatusCode;
        } catch (WebClientException webClientException) {
            logger.info("Request to send email was not successfully executed");
            return null;
        }

    }
}
