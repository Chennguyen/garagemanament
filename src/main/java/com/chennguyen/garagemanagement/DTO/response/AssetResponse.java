package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.AssetStatus;
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
public class AssetResponse {
    Long id;
    String assetCode;
    String name;
    String description;
    String category;
    String location;
    AssetStatus status;

    // Thông tin người đang giữ
    String currentHolderName;
    String currentHolderCode;

    BigDecimal purchasePrice;
    BigDecimal currentValue;
    LocalDate purchaseDate;

    // Bảo dưỡng
    LocalDate lastMaintenanceDate;
    LocalDate nextMaintenanceDate;
    String maintenanceNote;
}
