package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff;

    // Link tới lịch làm việc để biết Plan Start/End
    @OneToOne
    @JoinColumn(name = "work_schedule_id")
    WorkSchedule workSchedule;

    @Column(nullable = false)
    LocalDate date;

    // Giờ chấm công thực tế
    LocalTime checkInTime;
    LocalTime checkOutTime;

    // Số phút đi muộn / về sớm (Dùng để tính phạt)
    long lateMinutes;
    long earlyLeaveMinutes;

    // Giờ công được tính (Sau khi cắt gọt các phần dư thừa)
    Double validWorkingHours;

    @Enumerated(EnumType.STRING)
    AttendanceStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;
}
