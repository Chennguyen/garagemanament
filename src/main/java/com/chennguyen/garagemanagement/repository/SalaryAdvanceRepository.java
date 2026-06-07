package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.SalaryAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryAdvanceRepository extends JpaRepository<SalaryAdvance, String> {

    // 👇 Query quan trọng dùng trong PayrollService
    // Logic: Tìm các khoản ĐÃ DUYỆT (APPROVED) nằm trong khoảng thời gian tính lương
    @Query("SELECT s FROM SalaryAdvance s WHERE s.staff.id = :staffId " +
            "AND s.status = 'APPROVED' " +
            "AND s.requestDate BETWEEN :startDate AND :endDate")
    List<SalaryAdvance> findApprovedAdvancesInMonth(@Param("staffId") String staffId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    // Lấy lịch sử tạm ứng của 1 nhân viên
    List<SalaryAdvance> findByStaffIdOrderByRequestDateDesc(String staffId);
}