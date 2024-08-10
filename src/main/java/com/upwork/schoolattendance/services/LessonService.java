package com.upwork.schoolattendance.services;

import com.upwork.schoolattendance.model.Lesson;
import com.upwork.schoolattendance.repository.LessonRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LessonService {

    private static final int EARLY_CHECK_IN_ALLOWANCE = 20;
    private static final int DEADLINE_CHECK_IN_ALLOWANCE = 10;

    private final LessonRepository lessonRepository;

    public List<Lesson> getEligibleLessonsByClassroomQrCode(String qrCode) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusMinutes(EARLY_CHECK_IN_ALLOWANCE);
        LocalDateTime to = now.plusMinutes(DEADLINE_CHECK_IN_ALLOWANCE);
        log.debug("Finding lessons in interval from {} to {}",from,to);
        return lessonRepository.findAllByClassroomQrCodeAndStartTimeBetween(qrCode, from, to);
    }
}
