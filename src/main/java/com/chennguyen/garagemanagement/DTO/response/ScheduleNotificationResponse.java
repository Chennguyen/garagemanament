package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleNotificationResponse {
    Long id;
    String content;
    boolean isPinned;
    LocalDateTime createdAt; // Để hiện "Đăng lúc..."
}
