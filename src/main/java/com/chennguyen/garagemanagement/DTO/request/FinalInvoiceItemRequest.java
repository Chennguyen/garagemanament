package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.QuotationItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinalInvoiceItemRequest {
    @NotNull(message = "Item type is required")
    QuotationItemType type;

    Long productId;
    Long serviceCatalogItemId;

    @NotBlank(message = "Description is required")
    String description;

    @NotNull(message = "Quantity is required")
    Double quantity;

    @NotNull(message = "Unit price is required")
    BigDecimal unitPrice;
}
