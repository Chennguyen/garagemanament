package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.HandoverStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetHandoverResponse {
    Long id;

    // Tài sản
    Long assetId;
    String assetCode;
    String assetName;

    // Người nhận
    String staffId;
    String staffName;
    String employeeCode;

    // Người giao
    String handedOverByName;

    HandoverStatus status;

    LocalDateTime assignedAt;
    LocalDateTime acceptedAt;
    LocalDateTime returnedAt;

    // Kiểm kê
    LocalDateTime lastAuditedAt;
    boolean isAuditMissing;

    String note;
}
