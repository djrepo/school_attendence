package com.upwork.schoolattendance;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class SchoolAttendanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolAttendanceApplication.class, args);
    }

}
