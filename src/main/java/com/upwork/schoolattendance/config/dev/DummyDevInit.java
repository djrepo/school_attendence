package com.upwork.schoolattendance.config.dev;

import com.upwork.schoolattendance.model.Classroom;
import com.upwork.schoolattendance.model.Lesson;
import com.upwork.schoolattendance.model.Subject;
import com.upwork.schoolattendance.model.User;
import com.upwork.schoolattendance.repository.ClassroomRepository;
import com.upwork.schoolattendance.repository.LessonRepository;
import com.upwork.schoolattendance.repository.SubjectRepository;
import com.upwork.schoolattendance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@Profile({"dev","mock"})
public class DummyDevInit extends DummyObject {

    @Bean
    CommandLineRunner init(ClassroomRepository classroomRepository, SubjectRepository subjectRepository,
                           LessonRepository lessonRepository, UserRepository userRepository) {
        return (args) -> {
            List<Subject> subjects = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Subject subject = newSubject(i);
                subjectRepository.save(subject);
                subjects.add(subject);
            }

            List<Classroom> classrooms = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Classroom classroom = newClassroom(i);
                classroomRepository.save(classroom);
                classrooms.add(classroom);

                for (int j = 0; j < 7; j++) {
                    Lesson lesson = newLesson(j);

                    lesson.setClassroom(classroom);
                    lesson.setSubject(getRandomItem(subjects));

                    lessonRepository.save(lesson);

                }
            }
            for (int i = 0; i <= 5; i++) {
                User user = newUser(i);
                userRepository.save(user);
            }
        };
    }


    public static <T> T getRandomItem(List<T> list) {
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

}
