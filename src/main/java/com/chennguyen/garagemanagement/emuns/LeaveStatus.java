package com.chennguyen.garagemanagement.emuns;

public enum LeaveStatus {
    PENDING,  // Chờ duyệt
    APPROVED, // Đã duyệt (Sẽ sync sang lịch)
    REJECTED  // Từ chối
}
