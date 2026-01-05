package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.EmployeeType;
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
public class StaffRegistrationRequest {

    String facilityCode; // VD: "01"

    // Thông tin cá nhân
    String fullName;
    String phoneNumber;
    String address;
    String gender;
    LocalDate dob;

    // Thông tin công việc (HR)
    String role;       // VD: "ROLE_MECHANIC" (Chọn từ dropdown)
    String jobTitle;   // VD: "Thợ máy chính"
    BigDecimal salary;
    EmployeeType employeeType; // FULL_TIME, PART_TIME...
}
