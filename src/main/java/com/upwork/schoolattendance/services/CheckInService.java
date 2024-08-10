package com.upwork.schoolattendance.services;

import com.upwork.schoolattendance.model.exception.BadTokenException;
import com.upwork.schoolattendance.model.exception.ConflictException;
import com.upwork.schoolattendance.resources.errors.PropertyFile;
import com.upwork.schoolattendance.resources.model.CheckInRequest;
import com.upwork.schoolattendance.resources.model.CheckInTokenPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckInService {

    private final AttendanceService attendanceService;
    private final AuthJwtService authJwtService;

    public void checkIn(CheckInRequest input) {
        Long lessonId = input.getLessonId();
        Long userId = input.getUserId();
        String token = input.getToken();
        if (!authJwtService.validateToken(token, userId)) {
            log.info("Token expired or userId mismatch.");
            throw new BadTokenException(PropertyFile.ERROR_INVALID_TOKEN.key());
        }
        CheckInTokenPayload checkInTokenPayload = authJwtService.extractCheckInPayload(token);
        if (!isLessonIdValid(lessonId, checkInTokenPayload)) {
            log.info("Chosen lesson item not match your listing.");
            throw new BadTokenException(PropertyFile.ERROR_INVALID_LESSON_ITEM.key());
        }

        try {
            attendanceService.addAttendance(userId, lessonId);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.info("You are already checked in to this lesson, or lesson was removed.");
            throw new ConflictException(PropertyFile.ERROR_ALREADY_EXISTS.key());
        }

        log.info("userId : {} is successfully checkin to lessonId {}", userId, lessonId);
    }

    private boolean isLessonIdValid(Long lessonId, CheckInTokenPayload checkInTokenPayload) {
        if (checkInTokenPayload.getEligibleLessons() != null) {
            return checkInTokenPayload.getEligibleLessons().contains(lessonId);
        }
        return false;
    }


}
