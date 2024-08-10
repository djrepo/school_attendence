package com.upwork.schoolattendance.resources.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClassroomActivityResponse {
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    private String classroom;
    private List<CurrentLesson> currentLessons = new ArrayList<>();
    private String token;

    @Getter
    @Setter
    public static class CurrentLesson {
        private Long lessonId;
        private String subject;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimeFormat)
        private LocalDateTime startTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimeFormat)
        private LocalDateTime endTime;
    }


}
