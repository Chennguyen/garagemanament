package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.LeaveStatus;
import com.chennguyen.garagemanagement.emuns.LeaveType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff;

    @Column(nullable = false)
    LocalDate startDate;

    @Column(nullable = false)
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    LeaveType leaveType; // AL, UP, PH...

    String reason; // Lý do: "Về quê", "Đám cưới"...

    @Enumerated(EnumType.STRING)
    LeaveStatus status;

    // Người duyệt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    Staff approvedBy;

    String rejectReason; // Lý do từ chối (nếu có)

    @CreationTimestamp
    LocalDateTime createdAt;
}
