package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.AllowanceBasis;
import com.chennguyen.garagemanagement.emuns.AllowanceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "staff_allowances")
public class StaffAllowance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // 👇 Link tới Staff: Một nhân viên có nhiều loại phụ cấp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    Staff staff;

    @Enumerated(EnumType.STRING)
    AllowanceType allowanceType; // MEAL, FUEL...

    @Enumerated(EnumType.STRING)
    AllowanceBasis allowanceBasis; // MONTHLY, PER_DAY...

    @Column(nullable = false, precision = 19, scale = 2)
    BigDecimal amount; // Giá trị (VD: 40000 hoặc 500000)

    boolean active; // Tắt/Bật khoản phụ cấp này
}
