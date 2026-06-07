package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleResponse {
    String licensePlate;
    String vin;
    String make;
    String model;
    Integer productionYear;
    String color;
    String ownerId;
    String ownerName;
    String ownerPhoneNumber;
    String ownerAddress;
    String ownerTaxCode;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
