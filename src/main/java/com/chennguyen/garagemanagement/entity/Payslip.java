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

    // --- SNAPSHOT (LƯU VẾT) ---
    BigDecimal baseSalary;
    Double salaryCoefficient;
    BigDecimal hourlyRate;

    // 👇 MỚI: Snapshot Bank để xuất file lương
    String bankName;
    String bankAccountNumber;

    // --- CHẤM CÔNG (TIMEKEEPING) ---
    Double standardWorkDays;
    Double actualWorkDays;
    Double otNormalHours;
    Double otWeekendHours;
    Double otHolidayHours;

    // 👇 MỚI: Giờ làm đêm
    Double nightWorkHours;
    Double holidayNightWorkHours;

    // --- THU NHẬP (INCOME) ---
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
    // 👇 MỚI: Phạt & Thuế
    BigDecimal fineAmount;
    BigDecimal taxAmount;
    BigDecimal totalAdvances;

    @Column(nullable = false)
    BigDecimal totalDeductions;   // Tổng khấu trừ

    // --- THỰC LĨNH ---
    @Column(nullable = false)
    BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    PayslipStatus status;
    String note;
    @CreationTimestamp
    LocalDateTime createdAt;
}
