package com.upwork.schoolattendance.model.exception;

import com.upwork.schoolattendance.resources.errors.PropertyFile;

public class ConflictException extends RuntimeException {

    public ConflictException() {
        super(PropertyFile.ERROR_CONFLICT.key());
    }

    public ConflictException(String message) {
        super(message);
    }
}
