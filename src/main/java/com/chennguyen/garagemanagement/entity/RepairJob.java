package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.RepairJobStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "repair_jobs")
public class RepairJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    RepairOrder repairOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_catalog_item_id")
    ServiceCatalogItem serviceCatalogItem;

    @Column(nullable = false)
    String description;

    @Enumerated(EnumType.STRING)
    RepairJobStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_mechanic_id")
    Staff assignedMechanic;

    LocalDateTime assignedAt;
    LocalDateTime startedAt;
    LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    String mechanicNote;
}
