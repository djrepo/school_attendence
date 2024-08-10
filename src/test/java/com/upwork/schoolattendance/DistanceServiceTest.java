package com.upwork.schoolattendance;

import com.upwork.schoolattendance.services.DistanceService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DistanceServiceTest implements WithAssertions {

    @InjectMocks
    private DistanceService distanceService;


    @Test
    void testIsDistanceUnderDefinedThreshold() {
        boolean b = distanceService.isRemoteDistance(1D, 1D, 1D, 1D);
        Assertions.assertFalse(b);
    }

    @Test
    void testIsDistanceUnderDefinedThresholdWithRemoteArea() {
        boolean b = distanceService.isRemoteDistance(1D, 1D, 2D, 2D);
        Assertions.assertTrue(b);
    }


}