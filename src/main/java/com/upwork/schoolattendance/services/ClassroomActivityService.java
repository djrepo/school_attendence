package com.upwork.schoolattendance.services;

import com.upwork.schoolattendance.model.Attendance;
import com.upwork.schoolattendance.model.Lesson;
import com.upwork.schoolattendance.model.Classroom;
import com.upwork.schoolattendance.model.User;
import com.upwork.schoolattendance.model.exception.ConflictException;
import com.upwork.schoolattendance.model.exception.DistanceException;
import com.upwork.schoolattendance.model.exception.NoActivityFoundException;
import com.upwork.schoolattendance.repository.UserRepository;
import com.upwork.schoolattendance.resources.errors.PropertyFile;
import com.upwork.schoolattendance.resources.model.ClassroomActivityRequest;
import com.upwork.schoolattendance.resources.model.ClassroomActivityResponse;
import com.upwork.schoolattendance.resources.model.CheckInTokenPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClassroomActivityService {

    private final LessonService lessonService;
    private final UserRepository userRepository;
    private final AuthJwtService authJwtService;
    private final DistanceService distanceService;
    private final AttendanceService attendanceService;

    public ClassroomActivityResponse listClassroomActivity(ClassroomActivityRequest input) {
        List<Lesson> lessons = lessonService.getEligibleLessonsByClassroomQrCode(input.getQrCode());
        if (lessons.size() == 0) {
            log.info("No lesson found for qrCode {} and current time", input.getQrCode());
            throw new NoActivityFoundException();
        }
        Classroom classroom = lessons.get(0).getClassroom();
        boolean isRemote = distanceService.isRemoteDistance(classroom.getLatitude(), classroom.getLongitude(), input.getLatitude(), input.getLongitude());
        if (isRemote) {
            log.info("Trying to scan tokens from a remote location");
            throw new DistanceException();
        }

        List<Attendance> assignedOnDateTime = attendanceService.getAttendanceForUserAndDateTime(input.getUserId(), LocalDateTime.now());
        if (assignedOnDateTime.size()>0){
                throw new ConflictException(PropertyFile.ERROR_ANOTHER_ATTENDANCE.key());
        }
        Long userId = input.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Could not found given id: " + userId + " in Student"));

        return createClassroomActivityOutput(userId, classroom.getName(), lessons);
    }

    private ClassroomActivityResponse createClassroomActivityOutput(long userId, String classroomName, List<Lesson> lessons) {
        ClassroomActivityResponse output = new ClassroomActivityResponse();
        for (Lesson lesson : lessons) {
            output.setClassroom(classroomName);
            ClassroomActivityResponse.CurrentLesson currentLesson = new ClassroomActivityResponse.CurrentLesson();
            currentLesson.setSubject(lesson.getSubject().getName());
            currentLesson.setLessonId(lesson.getId());
            currentLesson.setStartTime(lesson.getStartTime());
            currentLesson.setEndTime(lesson.getEndTime());
            output.getCurrentLessons().add(currentLesson);
        }
        CheckInTokenPayload checkInTokenPayload = CheckInTokenPayload.builder()
                .eligibleLessons(lessons.stream().map(Lesson::getId).collect(Collectors.toList()))
                .build();
        output.setToken(authJwtService.generateToken(userId, checkInTokenPayload));
        return output;
    }

}
