package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Payslip;
import com.chennguyen.garagemanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, String> {

    // 👇 SỬA: Tìm theo Entity Staff thay vì String staffId
    // Dùng để check xem tháng này nhân viên đã có phiếu lương chưa
    Optional<Payslip> findByStaffAndSalaryPeriod(Staff staff, LocalDate salaryPeriod);

    // Lấy lịch sử lương của nhân viên (Mới nhất lên đầu)
    List<Payslip> findByStaffOrderBySalaryPeriodDesc(Staff staff);
}