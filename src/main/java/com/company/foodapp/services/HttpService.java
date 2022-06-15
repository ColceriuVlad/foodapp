package com.company.foodapp.services;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.UserDetails;
import com.company.foodapp.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;

@Service
public class HttpService {
    private WebClient webClient;
    private PropertiesFileReader propertiesFileReader;
    private CookieUtils cookieUtils;

    @Autowired
    public HttpService(PropertiesFileReader propertiesFileReader, WebClient.Builder webClientBuilder, CookieUtils cookieUtils) {
        this.propertiesFileReader = propertiesFileReader;
        this.webClient = initWebClient(webClientBuilder);
        this.cookieUtils = cookieUtils;
    }

    private WebClient initWebClient(WebClient.Builder webClientBuilder) {
        var baseUrl = propertiesFileReader.getProperty("APP_URL");
        var webClient = webClientBuilder.baseUrl(baseUrl).build();
        return webClient;
    }

    public UserDetails getUserDetails(HttpServletRequest httpServletRequest) {
        var cookie = cookieUtils.getCookieValue("token", httpServletRequest);

        var response = webClient
                .get()
                .uri("authorization/getCurrentUserDetails")
                .cookie("token", cookie)
                .retrieve()
                .bodyToMono(UserDetails.class)
                .block();
        return response;
    }
}
