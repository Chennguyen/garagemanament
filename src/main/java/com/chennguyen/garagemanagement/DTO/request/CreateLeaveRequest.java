package com.chennguyen.garagemanagement.DTO.request;


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
public class CreateLeaveRequest {
    String staffId;      // Nhân viên xin nghỉ
    LocalDate startDate;
    LocalDate endDate;
    LeaveType leaveType;
    String reason;
}
