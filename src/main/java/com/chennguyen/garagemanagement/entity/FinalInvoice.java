package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.InvoiceStatus;
import com.chennguyen.garagemanagement.emuns.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "final_invoices")
public class FinalInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    RepairOrder repairOrder;

    @Enumerated(EnumType.STRING)
    InvoiceStatus status;

    @Column(precision = 19, scale = 2)
    BigDecimal subtotalAmount;

    @Column(precision = 19, scale = 2)
    BigDecimal discountAmount;

    String discountCode;

    @Column(precision = 19, scale = 2)
    BigDecimal totalAmount;

    @Column(precision = 19, scale = 2)
    BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id")
    Staff cashier;

    @Column(columnDefinition = "TEXT")
    String note;

    LocalDateTime issuedAt;
    LocalDateTime paidAt;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
