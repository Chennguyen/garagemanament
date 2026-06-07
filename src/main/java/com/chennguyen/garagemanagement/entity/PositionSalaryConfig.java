package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.EmployeeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "position_salary_configs", uniqueConstraints = {
        // Đảm bảo không bị trùng cấu hình: 1 Chức vụ + 1 Loại hình chỉ có 1 mức lương gợi ý
        @UniqueConstraint(columnNames = {"jobTitle", "employeeType"})
})
public class PositionSalaryConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // 👇 Sửa thành String để khớp với field "jobTitle" bên Entity Staff
    @Column(nullable = false)
    String jobTitle; // VD: "Thợ máy chính", "Thu ngân"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    EmployeeType employeeType; // FULL_TIME, PART_TIME

    @Column(nullable = false, precision = 19, scale = 2)
    BigDecimal baseSalary; // Lương cơ bản đề xuất

    @Column(precision = 19, scale = 2)
    BigDecimal defaultResponsibilityAllowance; // Phụ cấp trách nhiệm mặc định
}
