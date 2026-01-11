package com.chennguyen.garagemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    // Mã lỗi hệ thống
    UNKNOWN_ERROR(9999, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_CREDENTIALS(1005, "Avail credentials", HttpStatus.UNAUTHORIZED),INVALID_TOKEN(1006, "Invalid token", HttpStatus.NOT_FOUND),

    // Mã lỗi nghiệp vụ (1xxx)
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1002, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),

    // 👇 CÁC MÃ LỖI MỚI CHO LOGIC ĐĂNG KÝ
    PHONE_EXISTED(1005, "Phone number already exists", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1006, "Email already exists", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1007, "Confirm password does not match", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1008, "Role not found", HttpStatus.NOT_FOUND),
    INVALID_FACILITY_CODE(1009, "Facility Code must have exactly 2 characters", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1010, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1011, "You do not have permission", HttpStatus.FORBIDDEN),
    PAYSLIP_EXISTED(1012, "Payslip already exists for this period", HttpStatus.BAD_REQUEST);

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
