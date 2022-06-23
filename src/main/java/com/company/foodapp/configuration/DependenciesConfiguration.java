package com.company.foodapp.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DependenciesConfiguration {
    @Bean
    @Scope("prototype")
    public Logger logger(InjectionPoint injectionPoint) {
        Class<?> classOnWired = injectionPoint.getMember().getDeclaringClass();
        return LoggerFactory.getLogger(classOnWired);
    }

    @Bean
    @Scope("prototype")
    public JwtBuilder jwtBuilder() {
        return Jwts.builder();
    }

    @Bean
    @Scope("prototype")
    public JwtParser jwtParser() {
        return Jwts.parser();
    }

    @Bean
    @Scope("singleton")
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
