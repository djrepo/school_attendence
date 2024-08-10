package com.upwork.schoolattendance.config.dev;

import com.upwork.schoolattendance.model.Classroom;
import com.upwork.schoolattendance.model.Lesson;
import com.upwork.schoolattendance.model.Subject;
import com.upwork.schoolattendance.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class DummyObject {

    public final static String FIRST_CLASSROOM_QR = "2f99d426-8ec6-4433-8ba9-2713787ba004";
    public final static String SECOND_CLASSROOM_QR = "dae631a8-f32d-4dab-a4a2-4c30dfc370ba";

    private final static String[] CLASSROOM_QRs = new String[]{
            FIRST_CLASSROOM_QR,
            SECOND_CLASSROOM_QR,
            "bef3e241-bc4d-4e20-a766-23979de0d47c",
            "a25caafa-f659-4e93-b7b1-2a410d87e002",
            "8be13fa1-16dc-4bd5-8c1f-5e5e0f787806",
            "0d5e6043-52f2-402e-aa85-b55431a48036",
            "a21582b0-3951-4632-9273-32dc64c9f20b",
            "187749ca-452f-415a-a7b0-8bc332012af0",
            "1055c18e-5768-42d6-96f4-6d87b39a534d"};

    protected Classroom newClassroom(int classRoomId) {
        String qrCode = UUID.randomUUID().toString();
        if (classRoomId < 9) {
            qrCode = CLASSROOM_QRs[classRoomId];
        }
        return Classroom.builder()
                .latitude(1D)
                .longitude(1D)
                .name("Class " + classRoomId + 1)
                .qrCode(qrCode)
                .build();
    }


    private final static String[] SUBJECT_NAMES = new String[]{"Mathematics", "English Language", "English Literature", "Biology", "Chemistry", "Physics", "History",
            "Geography", "French Language", "Spanish Language", "Religious Studies", "Civics/Citizenship", "Economics", "Sociology", "Psychology"};

    protected Subject newSubject(int i) {
        i = i % 15;
        return Subject.builder()
                .name(SUBJECT_NAMES[i])
                .build();
    }

    protected Lesson newLesson(int lessonOrder) {
        LocalDateTime startDate = calcStartDate(lessonOrder);
        LocalDateTime endDate = startDate.withMinute(50);
        return Lesson.builder()
                .startTime(startDate)
                .endTime(endDate)
                .build();
    }

    private LocalDateTime calcStartDate(int lessonOrder) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        if (hour > 3) {
            hour = hour - 3;
        }
        int targetHour = hour + lessonOrder;
        if (targetHour >= 24) {
            return now.plusDays(1).withHour(targetHour - 24);
        }
        return now.withHour(targetHour);
    }

    protected User newUser(int userOrder) {
        return User.builder()
                .name("Student " + userOrder + 1)
                .build();
    }

}
