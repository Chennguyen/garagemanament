package com.chennguyen.garagemanagement.DTO.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BonusRequest {
    String staffId; // ID nhân viên được thưởng
    BigDecimal amount;
    String reason;

    // Thêm trường này để biết thưởng cho tháng lương nào (VD: "10-2025")
    // Nếu null thì mặc định lấy tháng hiện tại
    String salaryPeriod;

}
