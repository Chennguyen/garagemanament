package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.AttendanceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceResponse {
    String staffName;    // Tên nhân viên (Để chào: Hello A)
    String employeeCode; // Mã nhân viên

    LocalDate date;
    LocalTime checkInTime;
    LocalTime checkOutTime;

    AttendanceStatus status; // ON_TIME, LATE...

    long lateMinutes;        // Để hiện cảnh báo: "Bạn đi trễ 5p"
    long earlyLeaveMinutes;

    String message;          // Thông báo custom (VD: "Check-in thành công!")
}
