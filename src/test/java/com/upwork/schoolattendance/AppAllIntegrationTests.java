package com.upwork.schoolattendance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upwork.schoolattendance.resources.model.CheckInRequest;
import com.upwork.schoolattendance.resources.model.ClassroomActivityRequest;
import com.upwork.schoolattendance.resources.model.ClassroomActivityResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.upwork.schoolattendance.config.dev.DummyObject.FIRST_CLASSROOM_QR;
import static com.upwork.schoolattendance.config.dev.DummyObject.SECOND_CLASSROOM_QR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({"h2", "mock"})
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class AppAllIntegrationTests {

    @Autowired
    protected MockMvc mvc;


    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${jwt.token.lifespan}")
    private int tokenLifespan;


    private final static String basePath = "/api/";

    protected <T> T testGet(String path, Object paramContent, Class<T> clazz) throws Exception {
        String s = testGet(path, paramContent);
        JsonNode getByIdNode = objectMapper.readTree(s);
        String userJsonStr = getByIdNode.toString();
        T output = objectMapper.readValue(userJsonStr, clazz);
        return output;
    }

    protected String testGet(String path, Object paramContent) throws Exception {
        if (paramContent == null) {
            MvcResult mvcResult = mvc.perform(get(basePath + path).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
            String contentResult = mvcResult.getResponse().getContentAsString();
            return contentResult;
        } else {
            MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(basePath + path);
            Map<String, Object> paramNames = objectMapper.convertValue(paramContent, Map.class);
            for (String key : paramNames.keySet()) {
                if (paramNames.get(key) != null)
                    mockHttpServletRequestBuilder.param(key, Objects.toString(paramNames.get(key)));
            }
            MvcResult mvcResult = mvc.perform(mockHttpServletRequestBuilder.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
            return mvcResult.getResponse().getContentAsString();
        }
    }

    protected MvcResult testPost(String path, Object content) throws Exception {
        if (content != null) {
            return mvc.perform(post(basePath + path).content(objectMapper.writeValueAsString(content)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        } else {
            return mvc.perform(post(basePath + path).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        }
    }

    protected <T> T testPost(String path, Object content, Class<T> clazz) throws Exception {
        MvcResult mvcResult = testPost(path, content);
        String s = mvcResult.getResponse().getContentAsString();
        JsonNode getByIdNode = objectMapper.readTree(s);
        String userJsonStr = getByIdNode.toString();
        T output = objectMapper.readValue(userJsonStr, clazz);
        return output;
    }

    protected void testPut(String path, Object content) throws Exception {
        MvcResult mvcResult = null;
        if (content != null) {
            mvcResult = mvc.perform(put(basePath + path).content(objectMapper.writeValueAsString(content)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        } else {
            mvcResult = mvc.perform(put(basePath + path).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        }
    }

    @Test
    void classroomActivityAndCheckInTest() throws Exception {
        long userId = 1l;
        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setUserId(userId);
        input.setLongitude(1d);
        input.setLatitude(1d);

        // error if qrcode is null
        AssertionError error1 = Assertions.assertThrows(AssertionError.class, () -> {
            ClassroomActivityResponse output = testPost("/listClassroomActivity", input, ClassroomActivityResponse.class);
        });

        input.setQrCode(FIRST_CLASSROOM_QR);
        ClassroomActivityResponse output = testPost("/listClassroomActivity", input, ClassroomActivityResponse.class);
        if (!output.getCurrentLessons().isEmpty()) {
            final ClassroomActivityResponse.CurrentLesson currentLesson = output.getCurrentLessons().get(0);
            final Long lessonId = currentLesson.getLessonId();
            final CheckInRequest checkInRequest = new CheckInRequest();
            checkInRequest.setUserId(userId);
            checkInRequest.setLessonId(lessonId);

            // fail if token is empty
            Assertions.assertThrows(AssertionError.class, () -> {
                testPut("/checkIn", checkInRequest);
            });

            checkInRequest.setToken(output.getToken());
            checkInRequest.setUserId(2l);
            // fail if read qr code belongs another person
            Assertions.assertThrows(AssertionError.class, () -> {
                testPut("/checkIn", checkInRequest);
            });

            // success if qrcode and time is ok
            checkInRequest.setUserId(userId);
            testPut("/checkIn", checkInRequest);

            // fail if two times checkin same qr code
            Assertions.assertThrows(AssertionError.class, () -> {
                testPut("/checkIn", checkInRequest);
            });


        }
        input.setQrCode(SECOND_CLASSROOM_QR);
        Assertions.assertThrows(AssertionError.class, () -> {
            testPost("/listClassroomActivity", input, ClassroomActivityResponse.class);
        });
    }

    @Test
    void classroomActivityAndCheckInTestWhenTokenExpired() throws Exception {
        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setLongitude(1d);
        input.setLatitude(1d);
        Long userId = 2l;
        input.setUserId(userId);
        input.setQrCode(FIRST_CLASSROOM_QR);
        ClassroomActivityResponse output = testPost("/listClassroomActivity", input, ClassroomActivityResponse.class);
        if (!output.getCurrentLessons().isEmpty()) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(tokenLifespan)+5);
            final ClassroomActivityResponse.CurrentLesson currentLesson = output.getCurrentLessons().get(0);
            final Long lessonId = currentLesson.getLessonId();
            final CheckInRequest checkInRequest = new CheckInRequest();
            checkInRequest.setUserId(userId);
            checkInRequest.setLessonId(lessonId);
            checkInRequest.setToken(output.getToken());

            // fail if time is expired
            Assertions.assertThrows(AssertionError.class, () -> {
                testPut("/checkIn", checkInRequest);
            });
        }
    }

    @Test
    void classroomActivityAndCheckInTestWhenRemoteArea() throws Exception {
        ClassroomActivityRequest input = new ClassroomActivityRequest();
        input.setLongitude(2d);
        input.setLatitude(1d);
        Long userId = 2l;
        input.setUserId(userId);
        input.setQrCode(FIRST_CLASSROOM_QR);
        // fail if scanning qr code in remote area
        Assertions.assertThrows(AssertionError.class, () -> {
            ClassroomActivityResponse output = testPost("/listClassroomActivity", input, ClassroomActivityResponse.class);
        });
    }

}

