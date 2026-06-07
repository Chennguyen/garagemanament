package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.ProductCategory;
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
public class ProductResponse {
    Long id;
    String sku;
    String name;
    ProductCategory category;
    String unit;
    String compatibleModels;
    BigDecimal costPrice;
    BigDecimal sellingPrice;
    Double minStockLevel;
    Double maxStockLevel;
    Double currentStock;
    LocalDateTime updatedAt;
}
