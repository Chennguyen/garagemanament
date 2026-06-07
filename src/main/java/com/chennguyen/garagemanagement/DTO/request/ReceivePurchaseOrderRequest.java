package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceivePurchaseOrderRequest {
    // Map ID của chi tiết đơn hàng (PurchaseOrderDetail.id) với số lượng thực nhận
    @NotNull(message = "Danh sách nhận hàng không được để trống")
    Map<Long, Double> receivedQuantities; 
}
