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
    // --- Định danh ---
    String id;            // UUID trong DB
    String employeeCode;  // Mã hiển thị (VD: 010001)
    String facilityCode;  // Mã cơ sở (thay cho shopId)

    // --- Thông tin Cá nhân ---
    String fullName;    // Giữ FullName cho chuẩn Entity
    String phoneNumber;
    String address;     // Quê quán / Địa chỉ (mapping với hometown)

    LocalDate dob;      // Ngày sinh
    String gender;      // Giới tính
    String avatar;      // Link ảnh đại diện
    String bio;         // Giới thiệu ngắn (nếu có)

    // --- Thông tin HR & Công việc ---
    String jobTitle;
    BigDecimal salary;
    LocalDate hireDate;
    StaffStatus status;         // ACTIVE, RESIGNED...
    EmployeeType employeeType;  // FULL_TIME, PART_TIME...

    // --- Phân quyền (Role) ---
    String roleName;        // ROLE_MECHANIC
    String roleDescription; // Thợ sửa chữa
}
