package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateAssetRequest {

    @NotBlank(message = "Mã tài sản không được bỏ trống")
    String assetCode;

    @NotBlank(message = "Tên tài sản không được bỏ trống")
    String name;

    String description;
    String category; // FIXED_ASSET, TOOL, UNIFORM
    String location;

    @NotNull(message = "Giá mua không được bỏ trống")
    BigDecimal purchasePrice;

    BigDecimal currentValue;
    LocalDate purchaseDate;

    LocalDate nextMaintenanceDate;
    String maintenanceNote;
}
