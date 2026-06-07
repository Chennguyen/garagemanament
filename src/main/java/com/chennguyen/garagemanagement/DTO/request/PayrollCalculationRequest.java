package com.chennguyen.garagemanagement.DTO.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollCalculationRequest {
    String staffId;
    LocalDate salaryPeriod; // VD: 2025-10-01

    // Chấm công
    Double standardWorkDays; // 26
    Double actualWorkDays;   // 24.5

    // OT & Đêm
    Double otNormalHours;
    Double otWeekendHours;
    Double otHolidayHours;
    Double nightWorkHours;        // Ca đêm thường
    Double holidayNightWorkHours; // Ca đêm lễ

    // Phạt (Nhập tay)
    BigDecimal fineAmount;
}
