package com.chennguyen.garagemanagement.emuns;

public enum TransactionType {
    INBOUND_PURCHASE,    // Nhập kho từ NCC
    OUTBOUND_SERVICE,    // Xuất kho cho Lệnh sửa chữa
    OUTBOUND_RETAIL,     // Xuất bán lẻ
    OUTBOUND_INTERNAL,   // Xuất sử dụng nội bộ / Tiêu hao xưởng
    ADJUSTMENT_UP,       // Điều chỉnh tăng (Sau khi kiểm kê)
    ADJUSTMENT_DOWN      // Điều chỉnh giảm (Sau khi kiểm kê)
}
