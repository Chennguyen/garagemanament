package com.chennguyen.garagemanagement.emuns;

public enum PurchaseOrderStatus {
    DRAFT,      // Đang lên danh sách
    ORDERED,    // Đã gửi NCC
    RECEIVED,   // Đã nhận hàng và nhập kho
    CANCELLED   // Hủy đơn
}
