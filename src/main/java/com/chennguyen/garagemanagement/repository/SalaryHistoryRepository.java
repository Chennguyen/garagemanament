package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.SalaryHistory;
import com.chennguyen.garagemanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, String> {

    // Lấy lịch sử thay đổi lương (Mới nhất lên đầu)
    List<SalaryHistory> findByStaffOrderByChangedAtDesc(Staff staff);
}
