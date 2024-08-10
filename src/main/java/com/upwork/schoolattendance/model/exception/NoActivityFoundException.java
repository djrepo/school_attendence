package com.upwork.schoolattendance.model.exception;

public class NoActivityFoundException extends RuntimeException {
    public NoActivityFoundException() {
        super("No activity found for your classroom");
    }

    public NoActivityFoundException(String message) {
        super(message);
    }
}
