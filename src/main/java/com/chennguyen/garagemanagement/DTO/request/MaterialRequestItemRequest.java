package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialRequestItemRequest {
    @NotNull(message = "Product id is required")
    Long productId;

    @NotNull(message = "Quantity is required")
    Double quantity;
}
