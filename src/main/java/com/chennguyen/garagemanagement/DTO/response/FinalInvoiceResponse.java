package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.InvoiceStatus;
import com.chennguyen.garagemanagement.emuns.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinalInvoiceResponse {
    Long id;
    Long repairOrderId;
    String licensePlate;
    InvoiceStatus status;
    BigDecimal subtotalAmount;
    BigDecimal discountAmount;
    String discountCode;
    BigDecimal totalAmount;
    BigDecimal paidAmount;
    PaymentMethod paymentMethod;
    String cashierName;
    String note;
    LocalDateTime issuedAt;
    LocalDateTime paidAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<FinalInvoiceItemResponse> items;
}
