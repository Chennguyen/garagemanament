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
    PAYSLIP_EXISTED(1012, "Payslip already exists for this period", HttpStatus.BAD_REQUEST),
    NO_SCHEDULE_TODAY(1030, "You do not have a work schedule today!", HttpStatus.BAD_REQUEST),
    INVALID_ATTENDANCE_ACTION(1031, "Invalid action (Only CHECK_IN or CHECK_OUT accepted)", HttpStatus.BAD_REQUEST),
    ALREADY_CHECKED_IN(1032, "You have already checked in!", HttpStatus.BAD_REQUEST),
    NOT_CHECKED_IN(1033, "You have not checked in yet! Please check in first.", HttpStatus.BAD_REQUEST),
    ALREADY_CHECKED_OUT(1034, "You have already completed your shift (Checked out)!", HttpStatus.BAD_REQUEST),

    // Asset Management
    ASSET_NOT_FOUND(1040, "Asset not found", HttpStatus.NOT_FOUND),
    ASSET_CODE_EXISTED(1041, "Asset code already exists", HttpStatus.BAD_REQUEST),
    ASSET_NOT_AVAILABLE(1042, "Asset is not available for handover (check status)", HttpStatus.BAD_REQUEST),
    HANDOVER_NOT_FOUND(1043, "Handover record not found", HttpStatus.NOT_FOUND),
    ASSET_ALREADY_ACCEPTED(1044, "Handover already accepted", HttpStatus.BAD_REQUEST),
    STAFF_STILL_HOLDING_ASSETS(1045, "Staff is still holding assets. Please retrieve all assets before finalizing.", HttpStatus.BAD_REQUEST),

    // Inventory Management
    PRODUCT_NOT_FOUND(1050, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_SKU_EXISTED(1051, "Product SKU already exists", HttpStatus.BAD_REQUEST),
    SUPPLIER_NOT_FOUND(1052, "Supplier not found", HttpStatus.NOT_FOUND),
    PO_NOT_FOUND(1053, "Purchase Order not found", HttpStatus.NOT_FOUND),
    NOT_ENOUGH_STOCK(1054, "Not enough stock available", HttpStatus.BAD_REQUEST),
    INVALID_TRANSACTION_TYPE(1055, "Invalid inventory transaction type", HttpStatus.BAD_REQUEST),
    PO_ALREADY_RECEIVED(1056, "Purchase order already received", HttpStatus.BAD_REQUEST),

    // Service Management
    VEHICLE_NOT_FOUND(1060, "Vehicle not found", HttpStatus.NOT_FOUND),
    VEHICLE_VIN_EXISTED(1061, "Vehicle VIN already exists", HttpStatus.BAD_REQUEST),
    SERVICE_CODE_EXISTED(1062, "Service catalog code already exists", HttpStatus.BAD_REQUEST),
    SERVICE_CATALOG_ITEM_NOT_FOUND(1063, "Service catalog item not found", HttpStatus.NOT_FOUND),
    SERVICE_VISIT_NOT_FOUND(1064, "Service visit not found", HttpStatus.NOT_FOUND),
    INVALID_SERVICE_WORKFLOW(1065, "Invalid service workflow for this action", HttpStatus.BAD_REQUEST),
    QUOTATION_NOT_FOUND(1066, "Quotation not found", HttpStatus.NOT_FOUND),
    INVALID_QUOTATION_STATUS(1067, "Invalid quotation status", HttpStatus.BAD_REQUEST),
    REPAIR_ORDER_NOT_FOUND(1068, "Repair order not found", HttpStatus.NOT_FOUND),
    REPAIR_JOB_NOT_FOUND(1069, "Repair job not found", HttpStatus.NOT_FOUND),
    MATERIAL_REQUEST_NOT_FOUND(1070, "Material request not found", HttpStatus.NOT_FOUND),
    INVALID_MATERIAL_QUANTITY(1071, "Invalid material issue quantity", HttpStatus.BAD_REQUEST),
    INVOICE_ALREADY_CREATED(1072, "Final invoice already created", HttpStatus.BAD_REQUEST),
    INVOICE_NOT_FOUND(1073, "Final invoice not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_ENOUGH(1074, "Paid amount is not enough", HttpStatus.BAD_REQUEST),
    INVOICE_NOT_PAID(1075, "Invoice is not paid yet", HttpStatus.BAD_REQUEST),
    GATE_PASS_NOT_FOUND(1076, "Gate pass not found", HttpStatus.NOT_FOUND),
    INVALID_GATE_PASS_STATUS(1077, "Invalid gate pass status", HttpStatus.BAD_REQUEST),
            ;

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
