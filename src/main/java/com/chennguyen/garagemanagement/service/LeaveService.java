package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.CreateLeaveRequest;
import com.chennguyen.garagemanagement.DTO.response.LeaveResponse;
import com.chennguyen.garagemanagement.emuns.LeaveStatus;
import com.chennguyen.garagemanagement.emuns.LeaveType;
import com.chennguyen.garagemanagement.emuns.ShiftType;
import com.chennguyen.garagemanagement.entity.LeaveRequest;
import com.chennguyen.garagemanagement.entity.Staff;
import com.chennguyen.garagemanagement.entity.WorkSchedule;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.LeaveRepository;
import com.chennguyen.garagemanagement.repository.StaffRepository;
import com.chennguyen.garagemanagement.repository.WorkScheduleRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LeaveService {
    LeaveRepository leaveRepository;
    StaffRepository staffRepository;
    WorkScheduleRepository scheduleRepository;

    // 1. TẠO ĐƠN NGHỈ PHÉP
    @Transactional
    public LeaveResponse createLeaveRequest(CreateLeaveRequest request) {
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .staff(staff)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .leaveType(request.getLeaveType())
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest saved = leaveRepository.save(leaveRequest);
        return mapToResponse(saved);
    }

    // 2. DUYỆT ĐƠN (SYNC SANG LỊCH)
    @Transactional
    public LeaveResponse approveLeave(Long leaveId) {
        // 👇 DÙNG HÀM HELPER MỚI Ở ĐÂY
        Staff manager = getCurrentAuthenticatedStaff();

        // Tìm đơn nghỉ
        LeaveRequest request = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Request is already processed");
        }

        // Cập nhật trạng thái
        request.setStatus(LeaveStatus.APPROVED);
        request.setApprovedBy(manager);

        // SYNC SANG WORK SCHEDULE
        syncToSchedule(request);

        return mapToResponse(leaveRepository.save(request));
    }

    // 3. TỪ CHỐI ĐƠN
    @Transactional
    public LeaveResponse rejectLeave(Long leaveId, String reason) {
        // 👇 DÙNG HÀM HELPER MỚI (Nếu cần lưu ai từ chối thì set vào)
        // Staff manager = getCurrentAuthenticatedStaff();

        LeaveRequest request = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        request.setStatus(LeaveStatus.REJECTED);
        request.setRejectReason(reason);

        return mapToResponse(leaveRepository.save(request));
    }

    // =======================================================
    // 👇 HÀM HELPER LẤY STAFF TỪ TOKEN (Code chuẩn bro cần)
    // =======================================================
    private Staff getCurrentAuthenticatedStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Với Staff, username trong Token chính là Mã Nhân Viên (employeeCode)
        String employeeCode = authentication.getName();

        return staffRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // --- HELPER: SYNC LOGIC ---
    private void syncToSchedule(LeaveRequest request) {
        LocalDate current = request.getStartDate();
        while (!current.isAfter(request.getEndDate())) {

            WorkSchedule schedule = scheduleRepository.findByStaffIdAndDate(request.getStaff().getId(), current)
                    .orElse(WorkSchedule.builder()
                            .staff(request.getStaff())
                            .date(current)
                            .startTime(LocalTime.of(0,0))
                            .endTime(LocalTime.of(0,0))
                            .build());

            if (request.getLeaveType() == LeaveType.OFF) {
                schedule.setShiftType(ShiftType.OFF);
            } else {
                schedule.setShiftType(ShiftType.LEAVE);
            }

            schedule.setNote(request.getLeaveType().name());
            schedule.setPublished(true);

            scheduleRepository.save(schedule);

            current = current.plusDays(1);
        }
    }

    // --- HELPER MAPPER ---
    private LeaveResponse mapToResponse(LeaveRequest entity) {
        return LeaveResponse.builder()
                .id(entity.getId())
                .staffName(entity.getStaff().getFullName())
                .employeeCode(entity.getStaff().getEmployeeCode())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .leaveType(entity.getLeaveType())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .approvedBy(entity.getApprovedBy() != null ? entity.getApprovedBy().getFullName() : null)
                .build();
    }
}
