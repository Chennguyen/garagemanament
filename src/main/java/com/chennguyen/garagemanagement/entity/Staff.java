package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.EmployeeType;
import com.chennguyen.garagemanagement.emuns.StaffStatus;
import jakarta.persistence.*;
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
@Entity
@Table(name = "staffs")
public class Staff {

    // --- ĐỊNH DANH HỆ THỐNG ---
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    Account account;

    @Column(unique = true, nullable = false)
    String employeeCode; // Mã NV (Logic cũ: 010001)

    String facilityCode; // Mã cơ sở (Logic cũ: thay cho shopId)// Mã cơ sở (Logic cũ: thay cho shopId)

    // --- Thông tin Cá nhân ---
    String fullName;
    String phoneNumber;

    LocalDate dob;
    String gender;
    String address;
    String bio; // Giới thiệu ngắn/Ghi chú
    String avatar;

    // --- THÔNG TIN NHÂN SỰ (HR) ---
    String jobTitle; // Ví dụ: "Thợ máy chính", "Lễ tân"

    @Enumerated(EnumType.STRING)
    StaffStatus status; // ACTIVE, RESIGNED...

    @Enumerated(EnumType.STRING)
    EmployeeType employeeType; // FULL_TIME, PART_TIME...

    LocalDate hireDate; // Ngày vào làm

    /**
     * Dùng BigDecimal cho tiền bạc để tránh lỗi làm tròn của double/float
     */
    @Column(precision = 19, scale = 2) // 19 số, 2 số thập phân
            BigDecimal salary;

    @Column(nullable = false, columnDefinition = "float default 0.0")
    Double annualLeaveBalance = 0.0; // Số ngày phép còn lại

    // 👇 MỚI: Thông tin ngân hàng
    String bankName;          // VD: MB Bank, Vietcombank
    String bankAccountNumber; // VD: 999999999
}
