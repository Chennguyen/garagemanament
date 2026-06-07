package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.GatePassStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gate_passes")
public class GatePass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    FinalInvoice invoice;

    @Column(unique = true, nullable = false)
    String code;

    @Column(columnDefinition = "TEXT")
    String qrContent;

    @Enumerated(EnumType.STRING)
    GatePassStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    Staff issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    Staff verifiedBy;

    @CreationTimestamp
    LocalDateTime issuedAt;

    LocalDateTime verifiedAt;
}
