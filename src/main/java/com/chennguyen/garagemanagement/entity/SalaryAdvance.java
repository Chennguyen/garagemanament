package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.AdvanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "salary_advances")
public class SalaryAdvance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // Người xin ứng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff;

    @Column(nullable = false)
    LocalDate requestDate; // Ngày xin ứng

    @Column(nullable = false, precision = 19, scale = 2)
    BigDecimal amount; // Số tiền

    String reason; // Lý do (VD: Cưới hỏi, Ốm đau...)

    @Enumerated(EnumType.STRING)
    AdvanceStatus status; // PENDING, APPROVED...

    // Người duyệt (Manager/Admin)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    Staff approvedBy;

    LocalDateTime approvedAt; // Ngày giờ duyệt

    @CreationTimestamp
    LocalDateTime createdAt;
}
