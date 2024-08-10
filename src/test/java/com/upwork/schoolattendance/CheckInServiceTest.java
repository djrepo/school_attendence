package com.upwork.schoolattendance;

import com.upwork.schoolattendance.model.*;
import com.upwork.schoolattendance.model.exception.BadTokenException;
import com.upwork.schoolattendance.model.exception.ConflictException;
import com.upwork.schoolattendance.resources.model.CheckInRequest;
import com.upwork.schoolattendance.resources.model.CheckInTokenPayload;
import com.upwork.schoolattendance.services.AuthJwtService;
import com.upwork.schoolattendance.services.CheckInService;
import com.upwork.schoolattendance.services.AttendanceService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CheckInServiceTest implements WithAssertions {

    @InjectMocks
    private CheckInService checkInService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private AuthJwtService authJwtService;

    private final List<Lesson> lessons = new ArrayList<>();

    private User user;

    private Lesson lesson;

    private CheckInTokenPayload checkInTokenPayload;

    @BeforeEach
    public void setup() {

        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Class1");
        classroom.setQrCode(UUID.randomUUID().toString());

        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Lecture1");

        lesson = new Lesson();
        lesson.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.withHour(9);
        LocalDateTime endDate = startDate.withMinute(50);
        lesson.setStartTime(now);
        lesson.setEndTime(endDate);
        lesson.setClassroom(classroom);
        lesson.setSubject(subject);

        lessons.add(lesson);

        user = new User();
        user.setId(1L);
        user.setName("Student");


        checkInTokenPayload = CheckInTokenPayload.builder()
                .eligibleLessons(List.of(1L))
                .build();


    }

    @Test
    void testCheckIn() {

        when(attendanceService.getAttendanceForUserAndDateTime(any(), any())).thenReturn(List.of());
        when(authJwtService.validateToken(any(), anyLong())).thenReturn(true);
        when(authJwtService.extractCheckInPayload(any())).thenReturn(checkInTokenPayload);


        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLessonId(1L);
        checkInRequest.setUserId(1L);

        checkInService.checkIn(checkInRequest);

    }

    @Test
    void testCheckInWithAlreadyCheckin() {
        when(attendanceService.getAttendanceForUserAndDateTime(any(), any())).thenReturn(List.of());
        doThrow(new DataIntegrityViolationException("Constraint violation")).when(attendanceService).addAttendance(anyLong(),anyLong());
        when(authJwtService.extractCheckInPayload(any())).thenReturn(checkInTokenPayload);
        when(authJwtService.validateToken(any(), anyLong())).thenReturn(true);

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLessonId(1L);
        checkInRequest.setUserId(1L);
        org.junit.jupiter.api.Assertions.assertThrows(ConflictException.class, () -> {
            checkInService.checkIn(checkInRequest);
        });

    }

    @Test
    void testCheckInWithTimeOverlapWithAnotherCheckIn() {

        doThrow(new DataIntegrityViolationException("")).when(attendanceService).addAttendance(anyLong(), anyLong());
        when(authJwtService.extractCheckInPayload(any())).thenReturn(checkInTokenPayload);
        when(authJwtService.validateToken(any(), anyLong())).thenReturn(true);

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLessonId(1L);
        checkInRequest.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(ConflictException.class, () -> {
            checkInService.checkIn(checkInRequest);
        });

    }

    @Test
    void testCheckInWithTimeExpired() {
        when(attendanceService.getAttendanceForUserAndDateTime(any(), any())).thenReturn(List.of());
        when(authJwtService.extractCheckInPayload(any())).thenReturn(checkInTokenPayload);
        when(authJwtService.validateToken(any(), anyLong())).thenReturn(false);

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLessonId(1L);
        checkInRequest.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(BadTokenException.class, () -> {
            checkInService.checkIn(checkInRequest);
        });

    }

    @Test
    void testCheckInWithNotAllowedLessonId() {
        when(attendanceService.getAttendanceForUserAndDateTime(any(), any())).thenReturn(List.of());
        CheckInTokenPayload payload = CheckInTokenPayload.builder()
                .eligibleLessons(List.of(2L)).build();
        when(authJwtService.extractCheckInPayload(any())).thenReturn(payload);
        when(authJwtService.validateToken(any(), anyLong())).thenReturn(true);

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLessonId(1L);
        checkInRequest.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(BadTokenException.class, () -> {
            checkInService.checkIn(checkInRequest);
        });

    }

    @Test
    void testCheckInWithNullAsEligibleLessons() {
        when(attendanceService.getAttendanceForUserAndDateTime(any(), any())).thenReturn(List.of());
        CheckInTokenPayload payload = CheckInTokenPayload.builder()
                .eligibleLessons(null).build();
        when(authJwtService.extractCheckInPayload(any())).thenReturn(payload);
        when(authJwtService.validateToken(any(), anyLong())).thenReturn(true);

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLessonId(1L);
        checkInRequest.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(BadTokenException.class, () -> {
            checkInService.checkIn(checkInRequest);
        });

    }

    @Test
    void testCheckInWithEmptyEligibleLessons() {
        when(attendanceService.getAttendanceForUserAndDateTime(any(), any())).thenReturn(List.of());
        CheckInTokenPayload payload = CheckInTokenPayload.builder()
                .eligibleLessons(List.of()).build();
        when(authJwtService.extractCheckInPayload(any())).thenReturn(payload);
        when(authJwtService.validateToken(any(), anyLong())).thenReturn(true);

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLessonId(1L);
        checkInRequest.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(BadTokenException.class, () -> {
            checkInService.checkIn(checkInRequest);
        });

    }

}