package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    // Tìm lịch của 1 nhân viên vào ngày cụ thể (để gán ca hoặc chấm công)
    Optional<WorkSchedule> findByStaffIdAndDate(String staffId, LocalDate date);

    // Lấy toàn bộ lịch trong tháng (Cho Grid View)
    @Query("SELECT w FROM WorkSchedule w WHERE w.date BETWEEN :startDate AND :endDate ORDER BY w.date ASC")
    List<WorkSchedule> findAllByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Lấy tất cả ca làm việc trong 1 ngày (để validate đủ người chưa)
    List<WorkSchedule> findByDate(LocalDate date);
}
