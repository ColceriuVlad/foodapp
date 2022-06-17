package com.company.foodapp.mappers;

import com.company.foodapp.dto.AuthenticationDetails;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class ClaimsToAuthenticationDetailsMapper {
    public AuthenticationDetails map(Claims claims) {
        return new AuthenticationDetails(claims.getSubject(), claims.get("role").toString(), claims.get("email").toString());
    }
}
