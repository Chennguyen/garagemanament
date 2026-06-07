package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.QuotationStatus;
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
public class QuotationResponse {
    Long id;
    Long serviceVisitId;
    String licensePlate;
    QuotationStatus status;
    BigDecimal subtotalParts;
    BigDecimal subtotalLabor;
    BigDecimal otherFees;
    BigDecimal discountAmount;
    BigDecimal totalAmount;
    String note;
    LocalDateTime sentAt;
    LocalDateTime approvedAt;
    LocalDateTime rejectedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<QuotationItemResponse> items;
}
