package com.chennguyen.garagemanagement.emuns;

public enum AttendanceStatus {
    ON_TIME,        // Đúng giờ (Trong khoảng ±15p)
    LATE,           // Đi muộn (> 15p)
    EARLY_LEAVE,    // Về sớm (> 15p)
    LATE_AND_EARLY, // Combo hủy diệt: Vừa muộn vừa sớm
    ABSENT          // Vắng mặt (Không check-in)
}
