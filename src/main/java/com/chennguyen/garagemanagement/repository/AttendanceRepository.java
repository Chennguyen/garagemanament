package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    // Tìm record chấm công của nhân viên trong ngày
    Optional<AttendanceRecord> findByStaffIdAndDate(String staffId, LocalDate date);
}
