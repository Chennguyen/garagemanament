package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @NotBlank(message = "SKU không được để trống")
    String sku;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;

    @NotNull(message = "Loại sản phẩm không được để trống")
    ProductCategory category;

    @NotBlank(message = "Đơn vị tính không được để trống")
    String unit;

    String compatibleModels;

    BigDecimal sellingPrice;

    Double minStockLevel;
    Double maxStockLevel;
}
