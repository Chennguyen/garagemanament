package com.chennguyen.garagemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inspection_damages")
public class InspectionDamage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_visit_id", nullable = false)
    ServiceVisit serviceVisit;

    String panel;
    String damageType;
    String severity;

    @Column(columnDefinition = "TEXT")
    String description;

    String photoUrl;
}
