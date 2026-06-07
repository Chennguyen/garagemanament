package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFinalInvoiceRequest {
    BigDecimal discountAmount;
    String discountCode;
    String note;

    @Valid
    List<FinalInvoiceItemRequest> additionalItems;
}
