package com.chennguyen.garagemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset_maintenances")
public class AssetMaintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    Staff reportedBy;

    String issueDescription; // Lỗi gì?
    String imageUrl;         // Ảnh chụp chỗ hỏng

    // Phần Manager xử lý
    boolean isResolved;
    boolean isEmployeeFault; // Lỗi do thợ làm hỏng/mất?
    BigDecimal deductionAmount; // Số tiền phạt/đền bù (Sync sang Payroll)

    @CreationTimestamp
    LocalDateTime reportedAt;
    LocalDateTime resolvedAt;
}
