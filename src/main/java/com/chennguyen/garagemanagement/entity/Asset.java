package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.AssetStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assets")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String assetCode; // Mã QR dán trên máy (VD: TOOL-001)

    @Column(nullable = false)
    String name; // Tên thiết bị (VD: Súng bắn ốc 1/2)

    String description; // Thông số kỹ thuật
    String category;    // Loại: FIXED_ASSET (Tài sản cố định), TOOL (Công cụ cá nhân), UNIFORM
    String location;    // Vị trí: "Xưởng A", "Tủ thợ 01"...

    @Enumerated(EnumType.STRING)
    AssetStatus status;

    // Nhân viên đang giữ (snapshot để query nhanh, source of truth vẫn là AssetHandover)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_holder_id")
    Staff currentHolder;

    BigDecimal purchasePrice; // Giá mua ban đầu
    BigDecimal currentValue;  // Giá trị còn lại (để đền bù)

    LocalDate purchaseDate;

    // Lịch bảo dưỡng định kỳ (cho thiết bị lớn như cầu nâng)
    LocalDate lastMaintenanceDate;   // Ngày bảo dưỡng lần cuối
    LocalDate nextMaintenanceDate;   // Ngày bảo dưỡng tiếp theo (Reminder)
    String maintenanceNote;          // Ghi chú: "Thay phớt", "Thay dầu"
}
