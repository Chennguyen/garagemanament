package com.chennguyen.garagemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bonuses")
public class Bonus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // 👇 Link trực tiếp tới Staff
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff;

    @Column(nullable = false, precision = 19, scale = 2)
    BigDecimal amount; // Số tiền thưởng

    @Column(nullable = false)
    String reason; // Lý do (VD: "Incentive tháng 11", "Thưởng tết")

    // Tháng tính thưởng (VD: "11-2025") - Để biết khoản này thuộc kỳ lương nào
    String salaryPeriod;

    @CreationTimestamp
    LocalDateTime createdAt; // Ngày tạo lệnh thưởng
}
