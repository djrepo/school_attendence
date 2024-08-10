package com.upwork.schoolattendance;

import com.upwork.schoolattendance.model.Classroom;
import com.upwork.schoolattendance.model.Lesson;
import com.upwork.schoolattendance.model.Subject;
import com.upwork.schoolattendance.repository.LessonRepository;
import com.upwork.schoolattendance.services.LessonService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.upwork.schoolattendance.config.dev.DummyObject.FIRST_CLASSROOM_QR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LessonServiceTest implements WithAssertions {

    @InjectMocks
    private LessonService lessonService;
    @Mock
    private LessonRepository lessonRepository;


    private final List<Lesson> lessons = new ArrayList<>();

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
    }
    @Test
    void testEligibleLessonsByClassroomQrCode(){
        when(lessonRepository.findAllByClassroomQrCodeAndStartTimeBetween(any(),any(),any())).thenReturn(lessons);
        List<Lesson> lessonList = lessonService.getEligibleLessonsByClassroomQrCode(FIRST_CLASSROOM_QR);
    }

    @Test
    void testEligibleLessonsByClassroomQrCodeWhenEmptyList(){
        when(lessonRepository.findAllByClassroomQrCodeAndStartTimeBetween(any(),any(),any())).thenReturn(List.of());
        List<Lesson> lessonList = lessonService.getEligibleLessonsByClassroomQrCode(FIRST_CLASSROOM_QR);
    }


}
