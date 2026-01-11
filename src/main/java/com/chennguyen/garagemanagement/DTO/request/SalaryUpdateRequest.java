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
public class SalaryUpdateRequest {
    String staffId;
    BigDecimal newSalary;
    String reason; // Lý do tăng/giảm lương
}
