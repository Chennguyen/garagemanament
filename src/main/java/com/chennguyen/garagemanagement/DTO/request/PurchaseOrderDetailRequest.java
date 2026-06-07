package com.chennguyen.garagemanagement.DTO.request;

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
public class PurchaseOrderDetailRequest {
    @NotNull(message = "Product ID không được để trống")
    Long productId;

    @NotNull(message = "Số lượng đặt không được để trống")
    Double orderedQuantity;

    @NotNull(message = "Đơn giá nhập không được để trống")
    BigDecimal unitPrice;
}
