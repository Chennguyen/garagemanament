package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.StaffAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffAllowanceRepository extends JpaRepository<StaffAllowance, Long> {

    // 👇 SỬA: Tìm bằng staffId (JPA hỗ trợ tìm qua quan hệ bằng dấu gạch dưới hoặc truyền Object)
    // Ở đây dùng findByStaff_Id để truyền string ID cho tiện
    List<StaffAllowance> findByStaff_Id(String staffId);
}
