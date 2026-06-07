package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpsertVehicleRequest {
    @NotBlank(message = "License plate is required")
    String licensePlate;

    String vin;
    String make;
    String model;
    Integer productionYear;
    String color;

    @NotBlank(message = "Customer id is required")
    String ownerId;

    String ownerTaxCode;
}
