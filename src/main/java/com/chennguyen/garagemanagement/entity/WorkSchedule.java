package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.ShiftType;
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
@Table(name = "work_schedules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"staff_id", "date"}) // Mỗi nhân viên chỉ có 1 ca trong 1 ngày
})
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff;

    @Column(nullable = false)
    LocalDate date; // Ngày làm việc (VD: 2025-11-20)

    @Column(nullable = false)
    LocalTime startTime; // Giờ bắt đầu kế hoạch (Plan Start)

    @Column(nullable = false)
    LocalTime endTime;   // Giờ kết thúc kế hoạch (Plan End)

    @Enumerated(EnumType.STRING)
    ShiftType shiftType; // NORMAL, HOLIDAY, OFF...

    String note; // Ghi chú công việc (Task Assignment)

    boolean isPublished; // Đã chốt lịch/Công khai chưa
    
    boolean isHoliday; // Cờ đánh dấu ngày Lễ/Tết để quét tính lương x3

    @CreationTimestamp
    LocalDateTime createdAt;
}
