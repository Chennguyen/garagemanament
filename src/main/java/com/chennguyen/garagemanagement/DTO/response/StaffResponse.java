package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.EmployeeType;
import com.chennguyen.garagemanagement.emuns.StaffStatus;
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
public class StaffResponse {
    String id;
    String employeeCode; // Mã NV (010001)
    String fullName;
    String phoneNumber;
    String facilityCode;

    // HR Info
    String jobTitle;
    StaffStatus status;
    EmployeeType employeeType;
    BigDecimal salary;
    LocalDate hireDate;

    // Role Info
    String roleName;        // ROLE_MECHANIC
    String roleDescription; // Thợ sửa chữa
}
