package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.RepairOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "repair_orders")
public class RepairOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_visit_id", nullable = false)
    ServiceVisit serviceVisit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    Quotation quotation;

    @Enumerated(EnumType.STRING)
    RepairOrderStatus status;

    @Builder.Default
    Boolean washRequested = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foreman_id")
    Staff foreman;

    LocalDateTime completedAt;
    LocalDateTime qcPassedAt;
    LocalDateTime washDoneAt;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
