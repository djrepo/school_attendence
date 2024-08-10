package com.upwork.schoolattendance.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upwork.schoolattendance.model.exception.BadTokenException;
import com.upwork.schoolattendance.resources.errors.PropertyFile;
import com.upwork.schoolattendance.resources.model.CheckInTokenPayload;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AuthJwtService {

    private final JwtService jwtService;

    private final ObjectMapper mapper;

    private final static String CHECKIN_PAYLOAD = "checkin_payload";


    public CheckInTokenPayload extractCheckInPayload(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new BadTokenException(PropertyFile.ERROR_EMPTY_TOKEN.key());
        }
        Function<Claims, Object> func = new Function<Claims, Object>(){
            @Override
            public Object apply(Claims claims) {
                return claims.get(CHECKIN_PAYLOAD);
            }
        };
        Object obj = jwtService.extractClaim(token, func);
        return mapper.convertValue(obj, CheckInTokenPayload.class);
    }


    public String generateToken(long userId, CheckInTokenPayload checkInTokenPayload) {
        return jwtService.generateToken(userId, Collections.singletonMap(CHECKIN_PAYLOAD, checkInTokenPayload));
    }


    public boolean validateToken(String token, long userId) {
        return jwtService.validateToken(token, userId);
    }
}
