package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueMaterialRequest {
    @NotNull(message = "Loại giao dịch không được để trống")
    TransactionType transactionType; // OUTBOUND_SERVICE, OUTBOUND_RETAIL, OUTBOUND_INTERNAL
    
    @NotBlank(message = "Mã tham chiếu (VD: Biển số xe, ID Lệnh sửa chữa) không được để trống")
    String referenceId;

    String note;

    // Map ID của sản phẩm (Product.id) với số lượng xuất
    @NotNull(message = "Danh sách xuất kho không được để trống")
    Map<Long, Double> issuedQuantities;
}
