package com.company.foodapp.utils;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtUtilsTest {
    private PropertiesFileReader propertiesFileReader;
    private JwtBuilder jwtBuilder;
    private JwtUtils jwtUtils;
    private JwtParser jwtParser;

    public JwtUtilsTest() {
        propertiesFileReader = mock(PropertiesFileReader.class);
        jwtBuilder = mock(JwtBuilder.class);
        jwtParser = mock(JwtParser.class);
        jwtUtils = new JwtUtils(propertiesFileReader, jwtBuilder, jwtParser);
    }

    @Test
    public void createJWT() {
        when(propertiesFileReader.getProperty("JWT_SECRET")).thenReturn("A3EEF668F2CF5061CFA55FA09A88A7CA50086C88374EC0AD24B70550B942CFB9");
        when(propertiesFileReader.getProperty("JWT_DURATION")).thenReturn("100000");

        var duration = Long.parseLong(propertiesFileReader.getProperty("JWT_DURATION"));

        var userDetails = mock(UserDetails.class);
        userDetails.subject = "test";
        userDetails.role = "tester";
        userDetails.duration = duration;

        var mockedBuilder = mock(JwtBuilder.class);
        var expectedToken = "weqfgertgrtop";

        when(jwtBuilder.setIssuedAt(anyObject())).thenReturn(mockedBuilder);
        when(mockedBuilder.setSubject(userDetails.subject)).thenReturn(mockedBuilder);
        when(mockedBuilder.claim("role", userDetails.role)).thenReturn(mockedBuilder);
        when(mockedBuilder.signWith(anyObject(), anyString())).thenReturn(mockedBuilder);
        when(jwtBuilder.compact()).thenReturn(expectedToken);

        var actualToken = jwtUtils.createJWT(userDetails);
        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void decodeJwt() {
        var token = "TEST_TOKEN";

        var parser = mock(JwtParser.class);
        var jws = mock(Jws.class);
        var claims = mock(Claims.class);


        when(propertiesFileReader.getProperty("JWT_SECRET")).thenReturn("A3EEF668F2CF5061CFA55FA09A88A7CA50086C88374EC0AD24B70550B942CFB9");
        when(jwtParser.setSigningKey(any(byte[].class))).thenReturn(parser);
        when(parser.parseClaimsJws(token)).thenReturn(jws);
        when(jws.getBody()).thenReturn(claims);
        when(claims.getSubject()).thenReturn("vlad");

        var actualClaims = jwtUtils.decodeJWT(token);
        Assertions.assertEquals("vlad", actualClaims.getSubject());
    }
}
