package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.KioskCheckInRequest;
import com.chennguyen.garagemanagement.DTO.response.AttendanceResponse;
import com.chennguyen.garagemanagement.emuns.AttendanceStatus;
import com.chennguyen.garagemanagement.entity.Account;
import com.chennguyen.garagemanagement.entity.AttendanceRecord;
import com.chennguyen.garagemanagement.entity.Staff;
import com.chennguyen.garagemanagement.entity.WorkSchedule;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.AccountRepository;
import com.chennguyen.garagemanagement.repository.AttendanceRepository;
import com.chennguyen.garagemanagement.repository.StaffRepository;
import com.chennguyen.garagemanagement.repository.WorkScheduleRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StaffRepository staffRepository;
    private final WorkScheduleRepository scheduleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper; // Inject ModelMapper vào Service

    private static final int GRACE_PERIOD_MINUTES = 15;

    @Transactional
    public AttendanceResponse handleKioskAttendance(KioskCheckInRequest request) { // Đổi kiểu trả về
        // ... (Logic xác thực user giữ nguyên) ...
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Staff staff = staffRepository.findByAccount(account)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // ... (Logic lấy lịch giữ nguyên) ...
        LocalDate today = LocalDate.now();
        WorkSchedule schedule = scheduleRepository.findByStaffIdAndDate(staff.getId(), today)
                .orElseThrow(() -> new AppException(ErrorCode.NO_SCHEDULE_TODAY));

        // ... (Logic xử lý Action giữ nguyên) ...
        AttendanceRecord savedRecord;
        if ("CHECK_IN".equalsIgnoreCase(request.getAction())) {
            savedRecord = processCheckIn(staff, schedule);
        } else if ("CHECK_OUT".equalsIgnoreCase(request.getAction())) {
            savedRecord = processCheckOut(staff, schedule);
        } else {
            throw new AppException(ErrorCode.INVALID_ATTENDANCE_ACTION);
        }

        // 👇 LOGIC MAPPING & MESSAGE CHUYỂN VÀO ĐÂY
        return mapToResponse(savedRecord);
    }

    // ... (Giữ nguyên các hàm private processCheckIn, processCheckOut, calculateValidHours) ...
    // ... Bro copy lại y chang các hàm đó ở bước trước nhé ...

    // 👇 Hàm Helper Map Entity -> DTO nằm gọn trong Service
    private AttendanceResponse mapToResponse(AttendanceRecord record) {
        AttendanceResponse response = modelMapper.map(record, AttendanceResponse.class);

        // Map các trường custom
        response.setStaffName(record.getStaff().getFullName());
        response.setEmployeeCode(record.getStaff().getEmployeeCode());

        // Tạo câu thông báo logic
        String msg = "Check-in/out successful!";
        if (record.getStatus() == AttendanceStatus.LATE) {
            msg = "Warning: You are late " + record.getLateMinutes() + " minutes!";
        } else if (record.getStatus() == AttendanceStatus.EARLY_LEAVE) {
            msg = "Warning: You are leaving early " + record.getEarlyLeaveMinutes() + " minutes!";
        } else if (record.getStatus() == AttendanceStatus.LATE_AND_EARLY) {
            msg = "Warning: Late In & Early Out!";
        }

        response.setMessage(msg);
        return response;
    }

    // ... (Giữ nguyên các hàm private processCheckIn, processCheckOut, calculateValidHours) ...
    private AttendanceRecord processCheckIn(Staff staff, WorkSchedule schedule) {
        // ... (Code cũ giữ nguyên)
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        attendanceRepository.findByStaffIdAndDate(staff.getId(), today).ifPresent(r -> {
            throw new AppException(ErrorCode.ALREADY_CHECKED_IN);
        });

        AttendanceRecord record = AttendanceRecord.builder()
                .staff(staff)
                .workSchedule(schedule)
                .date(today)
                .checkInTime(now)
                .status(AttendanceStatus.ON_TIME)
                .build();

        long diffMinutes = Duration.between(schedule.getStartTime(), now).toMinutes();

        if (diffMinutes > GRACE_PERIOD_MINUTES) {
            record.setLateMinutes(diffMinutes);
            record.setStatus(AttendanceStatus.LATE);
        } else {
            record.setLateMinutes(0);
        }

        return attendanceRepository.save(record);
    }

    private AttendanceRecord processCheckOut(Staff staff, WorkSchedule schedule) {
        // ... (Code cũ giữ nguyên)
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        AttendanceRecord record = attendanceRepository.findByStaffIdAndDate(staff.getId(), today)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_CHECKED_IN));

        if (record.getCheckOutTime() != null) {
            throw new AppException(ErrorCode.ALREADY_CHECKED_OUT);
        }

        record.setCheckOutTime(now);

        long diffMinutes = Duration.between(now, schedule.getEndTime()).toMinutes();

        if (diffMinutes > GRACE_PERIOD_MINUTES) {
            record.setEarlyLeaveMinutes(diffMinutes);
            if (record.getStatus() == AttendanceStatus.LATE) {
                record.setStatus(AttendanceStatus.LATE_AND_EARLY);
            } else {
                record.setStatus(AttendanceStatus.EARLY_LEAVE);
            }
        } else {
            record.setEarlyLeaveMinutes(0);
        }

        calculateValidHours(record, schedule);

        return attendanceRepository.save(record);
    }

    private void calculateValidHours(AttendanceRecord record, WorkSchedule schedule) {
        // ... (Code cũ giữ nguyên)
        LocalTime actualIn = record.getCheckInTime();
        LocalTime actualOut = record.getCheckOutTime();
        LocalTime planIn = schedule.getStartTime();
        LocalTime planOut = schedule.getEndTime();

        LocalTime effectiveIn = actualIn.isBefore(planIn) ? planIn : actualIn;
        LocalTime effectiveOut = actualOut.isAfter(planOut) ? planOut : actualOut;

        if (effectiveOut.isAfter(effectiveIn)) {
            long minutes = Duration.between(effectiveIn, effectiveOut).toMinutes();
            double hours = (double) minutes / 60.0;
            record.setValidWorkingHours(Math.round(hours * 100.0) / 100.0);
        } else {
            record.setValidWorkingHours(0.0);
        }
    }
}
