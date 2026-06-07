package com.chennguyen.garagemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "schedule_notifications")
public class ScheduleNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content; // Nội dung (VD: "Nhớ check in đúng giờ...")

    boolean isPinned; // Trạng thái ghim

    @CreationTimestamp
    LocalDateTime createdAt;
}
