package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateQuotationRequest {
    @NotNull(message = "Service visit id is required")
    Long serviceVisitId;

    BigDecimal discountAmount;
    String note;

    @Valid
    @NotEmpty(message = "Quotation items are required")
    List<QuotationItemRequest> items;
}
