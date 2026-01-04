package com.chennguyen.garagemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    USER_EXISTED(1001, "Userexists", HttpStatus.BAD_REQUEST),
    UNKNOWN_ERROR(9999, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(1008, "You not have permission", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1006, "Invalid token", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1005, "Avail credentials", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1009, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_USERNAME(1002, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST );

    private int code;
    private String message;
    private HttpStatusCode statuscode;
    ErrorCode(int code, String message, HttpStatusCode method) {
        this.code = code;
        this.message = message;
        this.statuscode = method;
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public HttpStatusCode getStatuscode() {return statuscode;}


}
