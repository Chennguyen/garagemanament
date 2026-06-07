package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateServiceCatalogItemRequest {
    @NotBlank(message = "Service code is required")
    String code;

    @NotBlank(message = "Service name is required")
    String name;

    String category;

    @NotNull(message = "Standard price is required")
    BigDecimal standardPrice;

    Integer estimatedMinutes;
    Boolean active;
}
