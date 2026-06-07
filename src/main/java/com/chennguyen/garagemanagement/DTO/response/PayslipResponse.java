package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.PayslipStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayslipResponse {
    String id;
    String staffName;
    String employeeCode;
    String jobTitle;
    String bankName;
    String bankAccountNumber;

    LocalDate salaryPeriod;
    PayslipStatus status;

    BigDecimal baseSalary;
    BigDecimal hourlyRate;

    // Chấm công
    Double actualWorkDays;
    Double totalHoursWorked; // Mới thêm
    Double otNormalHours;
    Double otWeekendHours;
    Double otHolidayHours;
    Double nightWorkHours;

    // Thu nhập
    BigDecimal totalAllowances;
    BigDecimal totalBonuses;
    BigDecimal grossSalary; // Tổng thu nhập (Đã bao gồm OT trong này)

    // Khấu trừ
    BigDecimal socialInsurance;
    BigDecimal healthInsurance;
    BigDecimal unemploymentInsurance;
    BigDecimal taxAmount;
    BigDecimal fineAmount;
    BigDecimal totalAdvances;
    BigDecimal totalDeductions;

    // Thực nhận
    BigDecimal netSalary;
}