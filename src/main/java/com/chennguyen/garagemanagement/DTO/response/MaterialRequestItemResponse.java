package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialRequestItemResponse {
    Long id;
    Long productId;
    String productName;
    String sku;
    Double requestedQuantity;
    Double issuedQuantity;
    Double currentStock;
}
