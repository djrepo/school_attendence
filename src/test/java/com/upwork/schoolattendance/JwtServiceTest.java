package com.upwork.schoolattendance;

import com.upwork.schoolattendance.resources.model.CheckInTokenPayload;
import com.upwork.schoolattendance.services.JwtService;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtServiceTest implements WithAssertions {

    private final static String SECRET = "rZagp2lvd/nIkaWw7CfvEZ9+PbXu9mzahHbr72JbMqA=";
    private final static int TOKEN_LIFE_SPAN = 20;
    @InjectMocks
    private JwtService jwtService = new JwtService(SECRET, 20);

    @Test
    void testGenerateToken() {
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        String token = jwtService.generateToken(1L, Collections.singletonMap("propertyName", checkInTokenPayload));
        Assertions.assertNotNull(token);
    }

    @Test
    void testValidateToken() {
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        String token = jwtService.generateToken(1L, Collections.singletonMap("propertyName", checkInTokenPayload));
        Assertions.assertTrue(jwtService.validateToken(token, 1L));
    }

    @Test
    void testValidateTokenWithWrongUser() {
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        String token = jwtService.generateToken(2L, Collections.singletonMap("propertyName", checkInTokenPayload));
        Assertions.assertFalse(jwtService.validateToken(token, 1L));
    }

    @Test
    void testValidateTokenWithNullToken() {
        Assertions.assertFalse(jwtService.validateToken(null, 1L));
    }
    @Test
    void testValidateTokenWithEmptyToken() {
        Assertions.assertFalse(jwtService.validateToken("", 1L));
    }

    @Test
    void testValidateTokenWithSubjectEmpty() {
        Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        Map<String, Object> claims = Collections.singletonMap("propertyName", checkInTokenPayload);
        String token = JwtService.createToken(secretKey,TOKEN_LIFE_SPAN,claims,"");

        Assertions.assertFalse(jwtService.validateToken(token, 1L));
    }
    @Test
    void testValidateTokenWithSubjectWrongType() {
        Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        Map<String, Object> claims = Collections.singletonMap("propertyName", checkInTokenPayload);
        String token = JwtService.createToken(secretKey,TOKEN_LIFE_SPAN,claims,"Not an integer");

        Assertions.assertFalse(jwtService.validateToken(token, 1L));
    }

    @Test
    void testValidateTokenWithExpiredExpiration() {
        Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        Map<String, Object> claims = Collections.singletonMap("propertyName", checkInTokenPayload);
        String token = JwtService.createToken(secretKey,-1,claims,"1");
        Assertions.assertFalse(jwtService.validateToken(token, 1L));
    }


}
