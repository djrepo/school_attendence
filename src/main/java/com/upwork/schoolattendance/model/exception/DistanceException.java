package com.upwork.schoolattendance.model.exception;

public class DistanceException extends RuntimeException {
    public DistanceException() {
        super("Distance is bigger than availability");
    }

    public DistanceException(String message) {
        super(message);
    }
}