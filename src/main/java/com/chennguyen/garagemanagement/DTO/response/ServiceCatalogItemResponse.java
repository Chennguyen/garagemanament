package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCatalogItemResponse {
    Long id;
    String code;
    String name;
    String category;
    BigDecimal standardPrice;
    Integer estimatedMinutes;
    Boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
