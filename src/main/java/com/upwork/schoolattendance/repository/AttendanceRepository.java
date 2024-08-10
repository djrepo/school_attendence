package com.upwork.schoolattendance.repository;

import com.upwork.schoolattendance.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {


    @Query("select e from Attendance e where e.user.id =:userId and  :specific between e.lesson.startTime and e.lesson.endTime")
    List<Attendance> listAttendanceOnSpecificTime(@Param("userId") Long userId, @Param("specific")LocalDateTime specific);


}