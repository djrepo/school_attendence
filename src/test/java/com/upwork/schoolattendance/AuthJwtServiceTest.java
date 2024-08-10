package com.upwork.schoolattendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upwork.schoolattendance.model.exception.BadTokenException;
import com.upwork.schoolattendance.resources.model.CheckInTokenPayload;
import com.upwork.schoolattendance.services.AuthJwtService;
import com.upwork.schoolattendance.services.JwtService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthJwtServiceTest implements WithAssertions {

    @InjectMocks
    private AuthJwtService authJwtService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ObjectMapper mapper;


    @Test
    void testGenerateToken() {

        when(jwtService.generateToken(anyLong(), any())).thenReturn("DUMMY");

        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        String token = authJwtService.generateToken(1L, checkInTokenPayload);
        Assertions.assertEquals("DUMMY", token);
    }

    @Test
    void testExtractCheckInPayload() {
        String token = "DUMMY";
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder().eligibleLessons(List.of(1L)).build();
        when(jwtService.extractClaim(eq(token),any())).thenReturn("{'':''");
        when(mapper.convertValue(any(),eq(CheckInTokenPayload.class))).thenReturn(checkInTokenPayload);
        CheckInTokenPayload extractedCheckInTokenPayload = authJwtService.extractCheckInPayload(token);
        Assertions.assertNotNull(extractedCheckInTokenPayload);
        Assertions.assertNotNull(extractedCheckInTokenPayload.getEligibleLessons());
        Assertions.assertEquals(checkInTokenPayload.getEligibleLessons().size(), extractedCheckInTokenPayload.getEligibleLessons().size());
        Assertions.assertArrayEquals(
                checkInTokenPayload.getEligibleLessons().stream()
                        .mapToLong(Long::longValue)
                        .toArray(),
                extractedCheckInTokenPayload.getEligibleLessons().stream()
                        .mapToLong(Long::longValue)
                        .toArray());
    }

    @Test
    public void testExtractCheckInPayloadWithEmptyToken() {
        org.junit.jupiter.api.Assertions.assertThrows(BadTokenException.class, () -> {
            authJwtService.extractCheckInPayload("");
        });

        org.junit.jupiter.api.Assertions.assertThrows(BadTokenException.class, () -> {
            authJwtService.extractCheckInPayload(null);
        });
    }

    @Test
    public void testExtractCheckInPayloadWithMissingClaim() {
        String token = "valid_token";

        when(jwtService.extractClaim(eq(token), any())).thenReturn(null);

        CheckInTokenPayload result = authJwtService.extractCheckInPayload(token);

        Assertions.assertNull(result);
    }

    @Test
    void testValidateToken() {
        when(jwtService.validateToken(any(),anyLong())).thenReturn(true);
        Assertions.assertTrue(authJwtService.validateToken("DUMMY", 1L));

    }

}
