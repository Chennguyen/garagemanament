package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.QuotationStatus;
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
@Table(name = "quotations")
public class Quotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_visit_id", nullable = false)
    ServiceVisit serviceVisit;

    @Enumerated(EnumType.STRING)
    QuotationStatus status;

    @Column(precision = 19, scale = 2)
    BigDecimal subtotalParts;

    @Column(precision = 19, scale = 2)
    BigDecimal subtotalLabor;

    @Column(precision = 19, scale = 2)
    BigDecimal otherFees;

    @Column(precision = 19, scale = 2)
    BigDecimal discountAmount;

    @Column(precision = 19, scale = 2)
    BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT")
    String note;

    LocalDateTime sentAt;
    LocalDateTime approvedAt;
    LocalDateTime rejectedAt;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
