package com.upwork.schoolattendance.resources.errors;

public enum PropertyFile {

    ERROR_CONFLICT("error.conflict"),
    ERROR_ANOTHER_ATTENDANCE("error.another-attendance"),
    ERROR_ALREADY_EXISTS("error.already-exists"),

    ERROR_BAD_TOKEN("error.bad-token"),

    ERROR_EMPTY_TOKEN("error.empty-token"),

    ERROR_INVALID_TOKEN("error.invalid-token"),

    ERROR_INVALID_LESSON_ITEM("error.invalid-lesson-item"),
    ERROR_ACCESS_DENIED("error.access-denied"),
    ERROR_BAD_CREDENTIALS("error.bad-credentials"),
    ERROR_ENTITY_NOT_FOUND("error.entity-not-found"),
    ERROR_MESSAGE_NOT_READABLE("error.message-not-readable"),
    ERROR_METHOD_ARGUMENT_NOT_VALID("error.method-argument-not-valid"),
    ERROR_METHOD_ARGUMENT_TYPE_MISMATCH("error.method-argument-type-mismatch"),
    ERROR_MISSING_PATH_VARIABLE("error.missing-path-variable"),
    ERROR_MISSING_REQUEST_HEADER("error.missing-request-header"),
    ERROR_MISSING_REQUEST_PARAMETER("error.missing-request-parameter"),
    ERROR_MISSING_REQUEST_QUERY("error.missing-request-query"),
    ERROR_MISSING_TOKEN_HEADER("error.missing-token-header"),
    ERROR_RECORD_NOT_FOUND("error.record-not-found"),
    ERROR_SERVER_ERROR("error.server-error");

    private final String key;

    PropertyFile(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    public String toString() {
        String var10000 = this.name();
        return "PropertyFile." + var10000 + "(key=" + this.key + ")";
    }
}
