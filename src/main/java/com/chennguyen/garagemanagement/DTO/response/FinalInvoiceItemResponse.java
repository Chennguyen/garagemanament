package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.QuotationItemType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinalInvoiceItemResponse {
    Long id;
    QuotationItemType type;
    Long productId;
    String productName;
    Long serviceCatalogItemId;
    String serviceCatalogCode;
    String description;
    Double quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
}
