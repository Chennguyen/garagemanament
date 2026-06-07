package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.emuns.LeaveStatus;
import com.chennguyen.garagemanagement.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    // 1. Lấy danh sách đơn nghỉ của 1 nhân viên (để hiện lịch sử cá nhân)
    List<LeaveRequest> findByStaffIdOrderByCreatedAtDesc(String staffId);

    // 2. Lấy danh sách đơn chờ duyệt (Cho Manager vào xem để duyệt)
    List<LeaveRequest> findByStatusOrderByCreatedAtAsc(LeaveStatus status);

    // 3. Lấy tất cả đơn nghỉ của 1 nhân viên theo trạng thái cụ thể
    List<LeaveRequest> findByStaffIdAndStatus(String staffId, LeaveStatus status);
}