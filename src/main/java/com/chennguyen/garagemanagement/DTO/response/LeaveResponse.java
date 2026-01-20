package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.LeaveStatus;
import com.chennguyen.garagemanagement.emuns.LeaveType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponse {
    Long id;
    String staffName;
    String employeeCode;

    LocalDate startDate;
    LocalDate endDate;
    LeaveType leaveType;
    String reason;

    LeaveStatus status;
    String approvedBy; // Tên người duyệt
}
