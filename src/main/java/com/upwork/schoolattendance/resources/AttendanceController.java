package com.upwork.schoolattendance.resources;

import com.upwork.schoolattendance.resources.model.CheckInRequest;
import com.upwork.schoolattendance.resources.model.ClassroomActivityRequest;
import com.upwork.schoolattendance.resources.model.ClassroomActivityResponse;
import com.upwork.schoolattendance.services.CheckInService;
import com.upwork.schoolattendance.services.ClassroomActivityService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
public class AttendanceController {
    private final ClassroomActivityService classroomActivityService;
    private final CheckInService checkInService;

    @ApiOperation(value = "List Classroom Activity")
    @PostMapping("/listClassroomActivity")
    public ResponseEntity<ClassroomActivityResponse> listClassroomActivity(@Valid @RequestBody ClassroomActivityRequest input) {
        log.info("user : {} , qrcode : {}", input.getUserId(), input.getQrCode());
        ClassroomActivityResponse resp = classroomActivityService.listClassroomActivity(input);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @ApiOperation(value = "Check In Classroom Activity")
    @PutMapping("/checkIn")
    public ResponseEntity<Void> checkIn(@Valid @RequestBody CheckInRequest input) {
        log.info("user : {} , lessonId - {} , token : {}", input.getUserId(), input.getLessonId(), input.getToken());
        checkInService.checkIn(input);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
