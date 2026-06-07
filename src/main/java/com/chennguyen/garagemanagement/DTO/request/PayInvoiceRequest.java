package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayInvoiceRequest {
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod;

    @NotNull(message = "Paid amount is required")
    BigDecimal paidAmount;

    String note;
}
