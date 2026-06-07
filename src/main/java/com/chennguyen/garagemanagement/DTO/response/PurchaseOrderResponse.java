package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.PurchaseOrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponse {
    Long id;
    Long supplierId;
    String supplierName;
    LocalDate orderDate;
    LocalDate expectedDeliveryDate;
    PurchaseOrderStatus status;
    BigDecimal totalAmount;
    String note;

    @Setter
    @Getter
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {
        Long id;
        Long productId;
        String productName;
        String sku;
        Double orderedQuantity;
        Double receivedQuantity;
        BigDecimal unitPrice;
        BigDecimal totalPrice;
    }

    List<Detail> details;
}
