package com.upwork.schoolattendance.resources.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInTokenPayload {
    private List<Long> eligibleLessons = new ArrayList<>();
}
