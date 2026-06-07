package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.PurchaseOrderStatus;
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
@Table(name = "purchase_orders")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

    LocalDate orderDate;
    LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    PurchaseOrderStatus status; // DRAFT, ORDERED, RECEIVED, CANCELLED

    BigDecimal totalAmount; // Tổng tiền nhập hàng

    String note;

    @CreationTimestamp
    LocalDateTime createdAt;
}
