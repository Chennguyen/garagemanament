package com.chennguyen.garagemanagement.emuns;

public enum ShiftType {
    NORMAL,     // Ca thường (Màu Xanh) - Lương x1.0
    HOLIDAY,    // Lễ/Tết (Màu Cam) - Lương x3.0 (Payroll sẽ check cái này)
    OFF,        // Ngày nghỉ (Màu Xám) - Không tính lương
    LEAVE       // Nghỉ phép (Sync từ module Leave)
}
