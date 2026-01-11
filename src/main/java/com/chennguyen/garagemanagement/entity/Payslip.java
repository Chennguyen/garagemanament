package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.PayslipStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payslips")
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // 👇 SỬA LẠI: Link trực tiếp sang Entity Staff
    // Hibernate sẽ lấy Staff.id (UUID) để lưu vào cột "staff_id" trong bảng payslips
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff;

    // 👇 Lưu thêm mã NV dạng String để tiện hiển thị/tìm kiếm lịch sử (Snapshot)
    // Lấy giá trị từ staff.getEmployeeCode() lúc tạo phiếu
    @Column(name = "snapshot_employee_code")
    String employeeCode;

    // Kỳ lương (Ví dụ: Tháng 11/2025 thì lưu ngày đầu tháng 2025-11-01)
    @Column(nullable = false)
    LocalDate salaryPeriod;

    // --- THU NHẬP (INCOME) ---
    BigDecimal baseSalary;        // Lương cơ bản (theo hợp đồng)
    BigDecimal totalAllowances;   // Tổng phụ cấp
    BigDecimal totalBonuses;      // Tổng thưởng
    Double totalHoursWorked;      // Tổng giờ làm (đối với Part-time)

    @Column(nullable = false)
    BigDecimal grossSalary;       // Tổng thu nhập trước thuế/khấu trừ

    // --- KHẤU TRỪ (DEDUCTIONS) ---
    BigDecimal socialInsurance;   // BHXH (8%)
    BigDecimal healthInsurance;   // BHYT (1.5%)
    BigDecimal unemploymentInsurance; // BHTN (1%)
    BigDecimal unionFee;          // Phí công đoàn (50k)

    @Column(nullable = false)
    BigDecimal totalDeductions;   // Tổng khấu trừ

    // --- THỰC NHẬN (NET) ---
    @Column(nullable = false)
    BigDecimal netSalary;         // Tiền thực nhận về túi

    // --- TRẠNG THÁI ---
    @Enumerated(EnumType.STRING)
    PayslipStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;      // Ngày chốt lương

    BigDecimal totalAdvances; // Tổng tiền đã ứng trong tháng
}
