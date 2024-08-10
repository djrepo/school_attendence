package com.upwork.schoolattendance.services;

import com.upwork.schoolattendance.model.Lesson;
import com.upwork.schoolattendance.model.User;
import com.upwork.schoolattendance.model.Attendance;
import com.upwork.schoolattendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EntityManager entityManager;

    public List<Attendance> getAttendanceForUserAndDateTime(Long userId, LocalDateTime time) {
        return attendanceRepository.listAttendanceOnSpecificTime(userId, time);
    }

    public void addAttendance(long userId, long lessonId) {
        User user = entityManager.getReference(User.class, userId);
        Lesson lesson = entityManager.getReference(Lesson.class, lessonId);
        Attendance attendance = new Attendance();
        attendance.setPresenceAt(LocalDateTime.now());
        attendance.setUser(user);
        attendance.setLesson(lesson);
        attendanceRepository.save(attendance);
    }


}
