package com.upwork.schoolattendance.services;

import com.upwork.schoolattendance.utils.DistanceCalculatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistanceService {
    private static final double MAX_DISTANCE_IN_METER = 10d;

    public boolean isRemoteDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        double meters = DistanceCalculatorUtil.calculateDistance(lat1, lon1, lat2, lon2);
        return meters > MAX_DISTANCE_IN_METER;
    }

}
