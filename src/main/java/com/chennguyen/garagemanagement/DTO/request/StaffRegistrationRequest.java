package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.EmployeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Mã cơ sở không được bỏ trống")
    String facilityCode; // VD: "01"

    // Thông tin cá nhân
    @NotBlank(message = "Họ tên không được bỏ trống")
    String fullName;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "^(03|05|07|08|09)\\d{8}$", message = "Số điện thoại không hợp lệ")
    String phoneNumber;

    @NotBlank(message = "Địa chỉ không được bỏ trống")
    String address;

    String gender;
    LocalDate dob;

    // Thông tin công việc (HR)
    @NotBlank(message = "Role không được bỏ trống")
    String role;       // VD: "ROLE_MECHANIC" (Chọn từ dropdown)

    String jobTitle;   // VD: "Thợ máy chính"
    BigDecimal salary;
    EmployeeType employeeType; // FULL_TIME, PART_TIME...

    // --- THÔNG TIN TỔ CHỨC ---
    String companyName;
    String costCenter;
    String payGroup;

    // --- THÔNG TIN NGÂN HÀNG ---
    String bankName;
    String bankAccountNumber;

    // --- THÔNG TIN THANH TOÁN ---
    Integer paymentDate;
}
