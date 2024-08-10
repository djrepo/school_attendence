package com.upwork.schoolattendance.resources.errors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.upwork.schoolattendance.model.exception.BadTokenException;
import com.upwork.schoolattendance.model.exception.ConflictException;
import com.upwork.schoolattendance.model.exception.DistanceException;
import com.upwork.schoolattendance.model.exception.NoActivityFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${spring.application.name:Application name not defined.}")
    private String serviceName;

    private static final String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";
    private static final String METHOD_ARGUMENT_NOT_VALID = "METHOD_ARGUMENT_NOT_VALID";
    private static final String MISSING_PATH_VARIABLE = "MISSING_PATH_VARIABLE";
    private static final String MISSING_REQUEST_PARAMETER = "MISSING_REQUEST_PARAMETER";
    private static final String MISSING_REQUEST_HEADER = "MISSING_REQUEST_HEADER";
    private static final String MESSAGE_NOT_READABLE = "MESSAGE_NOT_READABLE";
    private static final String SERVER_ERROR = "SERVER_ERROR";

    private final MessageSource messages;

    @ResponseStatus(OK)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleServerException(Exception ex, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleServerException: ", ex);

        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + SERVER_ERROR)
                .message(ExceptionUtils.getStackTrace(ex))
                .build();

        return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    public ResponseEntity<ErrorMessage> handleMissingHeaderException(MissingRequestHeaderException ex) {
        log.error("ResponseEntityExceptionHandler.handleMissingHeaderException: {}", ex.getMessage());

        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + MISSING_REQUEST_HEADER)
                .message(messages.getMessage(PropertyFile.ERROR_MISSING_REQUEST_HEADER.key(), null, Locale.ENGLISH))
//                .message(ExceptionUtils.getStackTrace(ex))
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleEntityNotFoundException: {}", ex.getMessage());

        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + ENTITY_NOT_FOUND)
                .message(ex.getMessage())
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }


    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> handleAlreadyExistsException(ConflictException ex, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleAlreadyExistsException: {}", ex.getMessage());
        var errorMessage = ErrorMessage.builder()
                .code(ex.getClass().getSimpleName())
                .message(messages.getMessage(ex.getMessage(), null, Locale.ENGLISH))
                .build();

        return getResponse(HttpStatus.CONFLICT, errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleBadRequestTraceException: {}", ex.getMessage());
        var errorMessage = ErrorMessage.builder()
                .code(ex.getClass().getSimpleName())
                .message(messages.getMessage(PropertyFile.ERROR_METHOD_ARGUMENT_TYPE_MISMATCH.key(), null, Locale.ENGLISH))
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(BadTokenException.class)
    public ResponseEntity<ErrorMessage> handleInvalidScheduleItemException(BadTokenException ex, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.InvalidScheduleItemException: {}", ex.getMessage());
        var errorMessage = ErrorMessage.builder()
                .code("INVALID_TOKEN")
                .message(messages.getMessage(ex.getMessage(), null, Locale.ENGLISH))
                .build();
        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(NoActivityFoundException.class)
    public ResponseEntity<ErrorMessage> handleDistanceException(NoActivityFoundException ex, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.NoActivityFound: {}", ex.getMessage());
        var errorMessage = ErrorMessage.builder()
                .code("EMPTY_LIST")
                .message(messages.getMessage(ex.getMessage(), null, Locale.ENGLISH))
                .build();
        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(DistanceException.class)
    public ResponseEntity<ErrorMessage> handleDistanceException(DistanceException ex, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.DistanceException: {}", ex.getMessage());
        var errorMessage = ErrorMessage.builder()
                .code("DISTANCE")
                .message(ex.getMessage())
                .build();
        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleBadRequestTraceException: {}", ex.getMessage());

        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + MISSING_REQUEST_PARAMETER)
                .message(messages.getMessage(PropertyFile.ERROR_MISSING_REQUEST_PARAMETER.key(), null, Locale.ENGLISH))
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleBadRequestTraceException: {}", ex.getMessage());

        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + MISSING_PATH_VARIABLE)
                .message(messages.getMessage(PropertyFile.ERROR_MISSING_PATH_VARIABLE.key(), null, Locale.ENGLISH))
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleBadRequestTraceException: {}", ex.getMessage());

        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + METHOD_ARGUMENT_NOT_VALID)
                .message("There are some validation errors.")
                .details(ex.getBindingResult()
                        .getFieldErrors().stream()
                        .map(fieldError -> String.format("%s::%s", fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList()))
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("ResponseEntityExceptionHandler.handleBindException: {}", ex.getMessage());

        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + METHOD_ARGUMENT_NOT_VALID)
                .message("There are some validation errors.")
                .details(ex.getBindingResult()
                        .getFieldErrors().stream()
                        .map(fieldError -> String.format("%s::%s", fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList()))
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String msg = null;
        Throwable cause = e.getCause();

        if (cause instanceof JsonParseException) {
            JsonParseException jpe = (JsonParseException) cause;
            msg = jpe.getOriginalMessage();
        }

        // special case of JsonMappingException below, too much class detail in error messages
        else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            if (mie.getPath() != null && mie.getPath().size() > 0) {
                msg = "Invalid request field: " + mie.getPath().get(0).getFieldName();
            }

            // just in case, haven't seen this condition
            else {
                msg = "Invalid request message";
            }
        } else if (cause instanceof JsonMappingException) {
            JsonMappingException jme = (JsonMappingException) cause;
            msg = jme.getOriginalMessage();
            if (jme.getPath() != null && jme.getPath().size() > 0) {
                msg = "Invalid request field: " + jme.getPath().get(0).getFieldName() +
                        ": " + msg;
            }
        }
        var errorMessage = ErrorMessage.builder()
                .code(serviceName.toUpperCase() + "_" + MESSAGE_NOT_READABLE)
                .message(messages.getMessage(PropertyFile.ERROR_MESSAGE_NOT_READABLE.key(), null, Locale.ENGLISH))
                .details(List.of(msg))
//                .message(ExceptionUtils.getStackTrace(ex))
                .build();

        return getResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    private <T> ResponseEntity<T> getResponse(HttpStatus httpStatus, T body) {
        switch (httpStatus) {
            case BAD_REQUEST:
            case CONFLICT:
            case UNPROCESSABLE_ENTITY:
                return ResponseEntity.status(httpStatus).contentType(APPLICATION_JSON).body(body);
            case INTERNAL_SERVER_ERROR:
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(body);
        }
    }


}
