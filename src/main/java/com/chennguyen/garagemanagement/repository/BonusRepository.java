package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Bonus;
import com.chennguyen.garagemanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, String> {

    // Lấy danh sách thưởng của nhân viên (mới nhất lên đầu)
    List<Bonus> findByStaffOrderByCreatedAtDesc(Staff staff);

    // 👇 QUAN TRỌNG: Tìm các khoản thưởng thuộc về một kỳ lương cụ thể (VD: "10-2025")
    // Dùng để tính tổng thưởng trong PayrollService
    @Query("SELECT b FROM Bonus b WHERE b.staff.id = :staffId AND b.salaryPeriod = :period")
    List<Bonus> findByStaffAndSalaryPeriod(@Param("staffId") String staffId, @Param("period") String period);
}
