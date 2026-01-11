package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.AllowanceBasis;
import com.chennguyen.garagemanagement.emuns.AllowanceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllowanceRequest {
    String staffId; // ID nhân viên được cấp phụ cấp
    AllowanceType allowanceType;   // MEAL, FUEL...
    AllowanceBasis allowanceBasis; // MONTHLY, PER_DAY...
    BigDecimal amount;
    boolean active; // Trạng thái bật/tắt
}
