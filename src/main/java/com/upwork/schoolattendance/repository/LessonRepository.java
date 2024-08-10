package com.upwork.schoolattendance.repository;

import com.upwork.schoolattendance.model.Lesson;
import com.upwork.schoolattendance.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    List<Lesson> findAllByClassroomQrCodeAndStartTimeBetween(String qrCode, LocalDateTime from, LocalDateTime to);

    List<Lesson> findAllByClassroom(Classroom classroom);

}