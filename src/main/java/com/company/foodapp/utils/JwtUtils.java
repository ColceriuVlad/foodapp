package com.company.foodapp.utils;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.JwtDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private PropertiesFileReader propertiesFileReader;
    private JwtBuilder jwtBuilder;
    private JwtParser jwtParser;

    @Autowired
    public JwtUtils(PropertiesFileReader propertiesFileReader, JwtBuilder jwtBuilder, JwtParser jwtParser) {
        this.propertiesFileReader = propertiesFileReader;
        this.jwtBuilder = jwtBuilder;
        this.jwtParser = jwtParser;
    }

    public String createJWT(JwtDetails jwtDetails) {
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(propertiesFileReader.getProperty("JWT_SECRET"));
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        jwtBuilder.setIssuedAt(now)
                .setSubject(jwtDetails.subject)
                .setIssuer(jwtDetails.id)
                .claim("role", jwtDetails.role)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (jwtDetails.duration > 0) {
            long expMillis = nowMillis + jwtDetails.duration;
            Date exp = new Date(expMillis);
            jwtBuilder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return jwtBuilder.compact();
    }

    public Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)

        var jwtSecret = propertiesFileReader.getProperty("JWT_SECRET");
        var convertedJwtSecret = DatatypeConverter.parseBase64Binary(jwtSecret);
        var parserWithSigningKey = jwtParser.setSigningKey(convertedJwtSecret);
        var parsedClaims = parserWithSigningKey.parseClaimsJws(jwt);
        var parsedClaimsBody = parsedClaims.getBody();

        return parsedClaimsBody;
    }
}
