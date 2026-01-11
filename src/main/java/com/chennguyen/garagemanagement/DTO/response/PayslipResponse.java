package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.PayslipStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PayslipResponse {
    String id;
    String staffId;
    String employeeCode;
    String staffName;
    LocalDate salaryPeriod;
    
    // Income
    BigDecimal baseSalary;
    BigDecimal totalAllowances;
    BigDecimal totalBonuses;
    Double totalHoursWorked;
    BigDecimal grossSalary;
    
    // Deductions
    BigDecimal socialInsurance;   // BHXH
    BigDecimal healthInsurance;   // BHYT
    BigDecimal unemploymentInsurance; // BHTN
    BigDecimal unionFee;
    BigDecimal totalDeductions;
    BigDecimal totalAdvances;
    
    // Net
    BigDecimal netSalary;
    
    // Status
    PayslipStatus status;
    LocalDateTime createdAt;
}
