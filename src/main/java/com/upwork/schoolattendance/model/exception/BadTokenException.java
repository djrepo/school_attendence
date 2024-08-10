package com.upwork.schoolattendance.model.exception;

import com.upwork.schoolattendance.resources.errors.PropertyFile;

public class BadTokenException extends RuntimeException {

    public BadTokenException() {
        super(PropertyFile.ERROR_BAD_TOKEN.key());
    }

    public BadTokenException(String message) {
        super(message);
    }
}
