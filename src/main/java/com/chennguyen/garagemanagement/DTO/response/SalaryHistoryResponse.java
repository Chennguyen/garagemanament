package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryHistoryResponse {
    String id;
    String staffId;
    String employeeCode;
    String staffName;
    BigDecimal oldSalary;
    BigDecimal newSalary;
    String reason;
    String updatedBy;
    LocalDateTime changedAt;
}
