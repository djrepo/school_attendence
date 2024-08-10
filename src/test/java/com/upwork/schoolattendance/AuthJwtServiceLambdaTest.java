package com.upwork.schoolattendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upwork.schoolattendance.resources.model.CheckInTokenPayload;
import com.upwork.schoolattendance.services.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthJwtServiceLambdaTest implements WithAssertions {

    private final static String SECRET = "rZagp2lvd/nIkaWw7CfvEZ9+PbXu9mzahHbr72JbMqA=";
    private final static int TOKEN_LIFE_SPAN = 20;
    @InjectMocks
    private AuthJwtService authJwtService ;
    @Spy
    private JwtService jwtServiceSpy = new JwtService(SECRET, 20);
    @Mock
    private ObjectMapper mapper;
    @Test
    public void testExtractCheckInPayloadValidToken() {
        // Given a valid token
        String token = "valid_token";

        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder()
                .eligibleLessons(List.of(1l))
                .build();
        Claims claims = new DefaultClaims(Collections.singletonMap("checkin_payload", "CheckInTokenPayloadAsJsonString"));

        when(mapper.convertValue(eq("CheckInTokenPayloadAsJsonString"), eq(CheckInTokenPayload.class))).thenReturn(checkInTokenPayload);
        doReturn(claims).when(jwtServiceSpy).extractAllClaims(token);

        CheckInTokenPayload result = authJwtService.extractCheckInPayload(token);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(checkInTokenPayload, result);
    }

}