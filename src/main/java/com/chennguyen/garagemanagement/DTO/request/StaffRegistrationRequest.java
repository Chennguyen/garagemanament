package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.EmployeeType;
import com.chennguyen.garagemanagement.emuns.Role;
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
    // Thông tin bắt buộc cũ
    String facilityCode;
    String fullName;
    String phoneNumber;
    String password;
    Role role;

    // Thông tin cá nhân
    LocalDate dob;
    String gender;
    String address;
    String avatar;
    String bio;

    // Thông tin HR (Mới thêm)
    String jobTitle;
    BigDecimal salary;
    LocalDate hireDate;
    EmployeeType employeeType;
    // Status thường mặc định là PROBATION hoặc ACTIVE khi tạo mới, không cần gửi từ FE
}
