package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.TransactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransactionResponse {
    Long id;
    Long productId;
    String productName;
    String sku;
    TransactionType type;
    Double quantity;
    Double stockAfterTransaction;
    String referenceId;
    String note;
    String performedByName;
    LocalDateTime transactionDate;
}
