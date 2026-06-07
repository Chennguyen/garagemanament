package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.ShiftType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkScheduleResponse {
    Long id;

    // Flatten dữ liệu Staff ra cho Frontend dễ dùng
    String staffId;
    String staffName;
    String jobTitle;

    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    ShiftType shiftType; // Để Frontend tô màu
    String note;         // Task được giao

    boolean isPublished;
}
