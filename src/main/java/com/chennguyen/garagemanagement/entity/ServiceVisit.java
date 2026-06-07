package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.FuelLevel;
import com.chennguyen.garagemanagement.emuns.ServiceVisitStatus;
import com.chennguyen.garagemanagement.emuns.WorkflowType;
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
@Table(name = "service_visits")
public class ServiceVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_license_plate", nullable = false)
    Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    WorkflowType workflowType;

    @Builder.Default
    Boolean washRequested = true;

    Integer odometer;

    @Enumerated(EnumType.STRING)
    FuelLevel fuelLevel;

    @Column(columnDefinition = "TEXT")
    String exteriorInspectionNote;

    @Column(columnDefinition = "TEXT")
    String customerRequest;

    @Column(columnDefinition = "TEXT")
    String advisorNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_advisor_id")
    Staff serviceAdvisor;

    @Enumerated(EnumType.STRING)
    ServiceVisitStatus status;

    @CreationTimestamp
    LocalDateTime checkInAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
