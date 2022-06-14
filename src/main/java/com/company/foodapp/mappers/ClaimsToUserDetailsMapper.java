package com.company.foodapp.mappers;

import com.company.foodapp.dto.UserDetails;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class ClaimsToUserDetailsMapper {
    public UserDetails map(Claims claims) {
        return new UserDetails(claims.getSubject(), claims.get("role").toString());
    }
}
