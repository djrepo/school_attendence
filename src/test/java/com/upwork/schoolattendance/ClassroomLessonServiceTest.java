package com.upwork.schoolattendance;

import com.upwork.schoolattendance.model.*;
import com.upwork.schoolattendance.model.exception.ConflictException;
import com.upwork.schoolattendance.model.exception.DistanceException;
import com.upwork.schoolattendance.model.exception.NoActivityFoundException;
import com.upwork.schoolattendance.repository.UserRepository;
import com.upwork.schoolattendance.resources.model.ClassroomActivityRequest;
import com.upwork.schoolattendance.resources.model.ClassroomActivityResponse;
import com.upwork.schoolattendance.services.*;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClassroomLessonServiceTest implements WithAssertions {

    @InjectMocks
    private ClassroomActivityService classroomActivityService;

    @Mock
    private LessonService lessonService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DistanceService distanceService;
    @Mock
    private AuthJwtService authJwtService;

    private final List<Lesson> lessons = new ArrayList<>();

    private User user;
    private Optional<User> studentOptional ;

    @BeforeEach
    public void setup() {

        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Class1" );
        classroom.setQrCode(UUID.randomUUID().toString());
        classroom.setLongitude(1d);
        classroom.setLatitude(1d);

        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Lecture1");

        Lesson lesson =new Lesson();
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

        studentOptional = Optional.of(user);
    }

    @Test
    void testListClassroomActivity() {
        when(lessonService.getEligibleLessonsByClassroomQrCode(any())).thenReturn(lessons);
        when(attendanceService.getAttendanceForUserAndDateTime(any(),any())).thenReturn(List.of());
        when(userRepository.findById(any())).thenReturn(studentOptional);
        when(distanceService.isRemoteDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(false);
        when(authJwtService.generateToken(anyLong(),any())).thenReturn("TOKEN");

        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setLongitude(1D);
        input.setLatitude(1D);
        input.setQrCode("abc");
        input.setUserId(1L);
        ClassroomActivityResponse output = classroomActivityService.listClassroomActivity(input);
        Assertions.assertEquals(1, output.getCurrentLessons().size());
        Assertions.assertTrue(StringUtils.isNotBlank(output.getToken()));
    }

    @Test
    void testListClassroomActivityWithEmptyList() {
        when(lessonService.getEligibleLessonsByClassroomQrCode(any())).thenReturn(List.of());
        when(attendanceService.getAttendanceForUserAndDateTime(any(),any())).thenReturn(List.of());
        when(userRepository.findById(any())).thenReturn(studentOptional);
        when(distanceService.isRemoteDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(false);

        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setLongitude(1D);
        input.setLatitude(1D);
        input.setQrCode("abc");
        input.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(NoActivityFoundException.class, () -> {
            classroomActivityService.listClassroomActivity(input);
        });

    }

    @Test
    void testListClassroomActivityWithRemoteLocation() {
        when(lessonService.getEligibleLessonsByClassroomQrCode(any())).thenReturn(lessons);
        when(attendanceService.getAttendanceForUserAndDateTime(any(),any())).thenReturn(List.of());
        when(userRepository.findById(any())).thenReturn(studentOptional);
        when(distanceService.isRemoteDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(true);

        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setLongitude(1D);
        input.setLatitude(1D);
        input.setQrCode("abc");
        input.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(DistanceException.class, () -> {
            classroomActivityService.listClassroomActivity(input);
        });

    }

    @Test
    void testListCurrentActivityWithNotExistedUser() {
        when(lessonService.getEligibleLessonsByClassroomQrCode(any())).thenReturn(lessons);
        when(attendanceService.getAttendanceForUserAndDateTime(any(),any())).thenReturn(List.of());
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        when(distanceService.isRemoteDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(false);

        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setLongitude(1D);
        input.setLatitude(1D);
        input.setQrCode("abc");
        input.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class, () -> {
            classroomActivityService.listClassroomActivity(input);
        });

    }

    @Test
    void testListCurrentActivityWithExistingAttendance() {
        when(lessonService.getEligibleLessonsByClassroomQrCode(any())).thenReturn(lessons);
        when(attendanceService.getAttendanceForUserAndDateTime(any(),any())).thenReturn(List.of(new Attendance()));
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        when(distanceService.isRemoteDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(false);

        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setLongitude(1D);
        input.setLatitude(1D);
        input.setQrCode("abc");
        input.setUserId(1L);

        org.junit.jupiter.api.Assertions.assertThrows(ConflictException.class, () -> {
            classroomActivityService.listClassroomActivity(input);
        });

    }
}