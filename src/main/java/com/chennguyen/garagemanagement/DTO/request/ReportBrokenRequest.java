package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportBrokenRequest {

    @NotNull(message = "Asset ID không được bỏ trống")
    Long assetId;

    @NotBlank(message = "Mô tả lỗi không được bỏ trống")
    String issueDescription;

    String imageUrl; // URL ảnh chụp hư hỏng

    // Chỉ Manager điền khi resolve
    boolean isEmployeeFault;
    BigDecimal deductionAmount; // Số tiền đền bù nếu lỗi do thợ
}
