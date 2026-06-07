package com.chennguyen.garagemanagement.emuns;

public enum AssetStatus {
    AVAILABLE,      // Đang trong kho, sẵn sàng cấp phát
    PENDING_ACCEPT, // Đã giao, chờ thợ bấm xác nhận
    IN_USE,         // Đang được thợ sử dụng
    BROKEN,         // Đang hỏng chờ sửa
    LIQUIDATED      // Đã thanh lý/Vứt bỏ
}
