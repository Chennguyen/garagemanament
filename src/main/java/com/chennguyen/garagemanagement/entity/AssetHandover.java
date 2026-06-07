package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.HandoverStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset_handovers")
public class AssetHandover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff; // Người cầm đồ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handed_over_by")
    Staff handedOverBy; // Admin/Thủ kho tạo phiếu bàn giao

    @Enumerated(EnumType.STRING)
    HandoverStatus status;

    @CreationTimestamp
    LocalDateTime assignedAt; // Ngày giao
    LocalDateTime acceptedAt; // Ngày thợ xác nhận nhận đồ
    LocalDateTime returnedAt; // Ngày trả đồ

    // Kiểm kê (Audit)
    LocalDateTime lastAuditedAt;  // Lần cuối thợ quét QR xác nhận còn giữ
    boolean isAuditMissing;       // true = không quét -> Nghi ngờ thất lạc

    String note;
}
