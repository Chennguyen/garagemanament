package com.chennguyen.garagemanagement.emuns;

public enum AdvanceStatus {
    PENDING,    // Chờ duyệt
    APPROVED,   // Đã duyệt (Sẽ bị trừ vào kỳ lương tới)
    REJECTED,   // Từ chối
    PAID        // Đã trừ xong (Sau khi chốt lương thì update về trạng thái này)
}
