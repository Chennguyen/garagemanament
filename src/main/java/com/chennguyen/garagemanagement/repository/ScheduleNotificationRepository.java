package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.ScheduleNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleNotificationRepository extends JpaRepository<ScheduleNotification, Long> {
    // Lấy các thông báo được ghim, mới nhất lên đầu
    List<ScheduleNotification> findByIsPinnedTrueOrderByCreatedAtDesc();
}
