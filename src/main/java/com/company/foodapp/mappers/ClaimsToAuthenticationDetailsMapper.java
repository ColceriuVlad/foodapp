package com.company.foodapp.mappers;

import com.company.foodapp.dto.AuthenticationDetails;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClaimsToAuthenticationDetailsMapper {
    private Logger logger;

    @Autowired
    public ClaimsToAuthenticationDetailsMapper(Logger logger) {
        this.logger = logger;
    }

    public AuthenticationDetails map(Claims claims) {
        if (claims != null) {
            logger.info("Authentication details were retrieved");

            return new AuthenticationDetails(
                    claims.getSubject(),
                    claims.get("role").toString(),
                    claims.get("email").toString());
        } else {
            logger.info("Retrieved no authentication details");

            return null;
        }
    }
}
