package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePurchaseOrderRequest {
    @NotNull(message = "Nhà cung cấp không được để trống")
    Long supplierId;

    LocalDate expectedDeliveryDate;

    String note;

    List<PurchaseOrderDetailRequest> details;
}
