package com.upwork.schoolattendance.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health-check-status")
@CrossOrigin
public class HealthCheckStatusController {
    @GetMapping
    public ResponseEntity<Map<String, String>> get() {
        log.info("HealthCheckStatusController invoked ");
        Map<String, String> hashMap = Collections.unmodifiableMap(new HashMap<>() {{
            put("version", "0.0.1-SNAPSHOT");
            put("timestamp", new Date().toString());
        }});
        return ResponseEntity.ok(hashMap);
    }
}
