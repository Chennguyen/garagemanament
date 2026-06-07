package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetMaintenanceResponse {
    Long id;
    Long assetId;
    String assetCode;
    String assetName;

    String reportedByName;
    String reportedByCode;

    String issueDescription;
    String imageUrl;

    boolean isResolved;
    boolean isEmployeeFault;
    BigDecimal deductionAmount;

    LocalDateTime reportedAt;
    LocalDateTime resolvedAt;
}
