package com.upwork.schoolattendance;

import com.upwork.schoolattendance.model.*;
import com.upwork.schoolattendance.repository.AttendanceRepository;
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

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttendanceServiceTest implements WithAssertions {

    @InjectMocks
    private AttendanceService attendanceService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private AttendanceRepository attendanceRepository;

    private final List<Lesson> lessons = new ArrayList<>();

    private User user;

    private Lesson lesson;


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

    }

    @Test
    void testAddAttendance() {
        when(entityManager.getReference(eq(User.class), anyLong())).thenReturn(user);
        when(entityManager.getReference(eq(Lesson.class), anyLong())).thenReturn(lesson);
        when(attendanceRepository.save(any())).thenReturn(null);
        attendanceService.addAttendance(1l,1l);
    }

    @Test
    void testAddAttendanceWithConstraintViolation() {
        when(entityManager.getReference(eq(User.class), anyLong())).thenReturn(user);
        when(entityManager.getReference(eq(Lesson.class), anyLong())).thenReturn(lesson);
        when(attendanceRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));

        org.junit.jupiter.api.Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            attendanceService.addAttendance(1l,1l);
        });

    }

    @Test
    void testGetAttendanceForUserAndDateTime() {
        when(attendanceRepository.listAttendanceOnSpecificTime(anyLong(), any())).thenReturn(List.of(new Attendance()));

        attendanceService.getAttendanceForUserAndDateTime(1l, LocalDateTime.now());
    }

}