package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.ShiftType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftAssignmentRequest {
    String staffId;      // ID nhân viên
    LocalDate date;      // Ngày xếp lịch
    LocalTime startTime; // Giờ bắt đầu (VD: 08:00)
    LocalTime endTime;   // Giờ kết thúc (VD: 17:00)
    ShiftType shiftType; // NORMAL, HOLIDAY...
    String note;         // Giao việc (Task)
}
