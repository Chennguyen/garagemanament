package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.ShiftAssignmentRequest;
import com.chennguyen.garagemanagement.DTO.response.ScheduleNotificationResponse;
import com.chennguyen.garagemanagement.DTO.response.WorkScheduleResponse;
import com.chennguyen.garagemanagement.emuns.ShiftType;
import com.chennguyen.garagemanagement.entity.ScheduleNotification;
import com.chennguyen.garagemanagement.entity.Staff;
import com.chennguyen.garagemanagement.entity.WorkSchedule;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.ScheduleNotificationRepository;
import com.chennguyen.garagemanagement.repository.StaffRepository;
import com.chennguyen.garagemanagement.repository.WorkScheduleRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleService {
    private final WorkScheduleRepository scheduleRepository;
    private final StaffRepository staffRepository;
    private final ScheduleNotificationRepository notificationRepository;

    // 1. GÁN CA LÀM VIỆC (Trả về Response DTO)
    @Transactional
    public WorkScheduleResponse assignShift(ShiftAssignmentRequest request) {
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        WorkSchedule schedule = scheduleRepository.findByStaffIdAndDate(staff.getId(), request.getDate())
                .orElse(new WorkSchedule());

        schedule.setStaff(staff);
        schedule.setDate(request.getDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setShiftType(request.getShiftType());
        schedule.setNote(request.getNote());
        schedule.setPublished(true);

        WorkSchedule savedSchedule = scheduleRepository.save(schedule);

        // Map sang DTO trước khi trả về
        return mapToScheduleResponse(savedSchedule);
    }

    // 2. VALIDATE CA TRỰC (Giữ nguyên logic, trả về boolean thì ko cần DTO)
    public boolean validateDailyCoverage(LocalDate date) {
        List<WorkSchedule> shifts = scheduleRepository.findByDate(date);
        boolean hasManagerOrCashier = false;
        boolean hasMechanic = false;

        for (WorkSchedule shift : shifts) {
            if (shift.getShiftType() == ShiftType.OFF || shift.getShiftType() == ShiftType.LEAVE) continue;

            String jobTitle = shift.getStaff().getJobTitle();
            if (jobTitle == null) continue;
            jobTitle = jobTitle.toUpperCase();

            if (jobTitle.contains("MANAGER") || jobTitle.contains("CASHIER") || jobTitle.contains("THU NGÂN") || jobTitle.contains("QUẢN LÝ")) {
                hasManagerOrCashier = true;
            }
            if (jobTitle.contains("MECHANIC") || jobTitle.contains("THỢ") || jobTitle.contains("KỸ THUẬT")) {
                hasMechanic = true;
            }
        }
        return hasManagerOrCashier && hasMechanic;
    }

    // 3. LẤY LỊCH THÁNG (Trả về List<Response DTO>)
    public List<WorkScheduleResponse> getMonthlySchedule(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<WorkSchedule> schedules = scheduleRepository.findAllByDateBetween(startDate, endDate);

        // Stream để map từng Entity sang DTO
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    // 4. GHIM THÔNG BÁO (Trả về Notification Response)
    @Transactional
    public ScheduleNotificationResponse pinMessage(String content) {
        List<ScheduleNotification> pinnedList = notificationRepository.findByIsPinnedTrueOrderByCreatedAtDesc();
        for (ScheduleNotification noti : pinnedList) {
            noti.setPinned(false);
            notificationRepository.save(noti);
        }

        ScheduleNotification newNoti = ScheduleNotification.builder()
                .content(content)
                .isPinned(true)
                .build();

        ScheduleNotification saved = notificationRepository.save(newNoti);
        return mapToNotificationResponse(saved);
    }

    public ScheduleNotificationResponse getPinnedMessage() {
        ScheduleNotification noti = notificationRepository.findByIsPinnedTrueOrderByCreatedAtDesc()
                .stream().findFirst().orElse(null);

        if (noti == null) return null;
        return mapToNotificationResponse(noti);
    }

    // =======================================================
    // PRIVATE MAPPER METHODS (Giúp code gọn hơn)
    // =======================================================

    private WorkScheduleResponse mapToScheduleResponse(WorkSchedule entity) {
        return WorkScheduleResponse.builder()
                .id(entity.getId())
                .staffId(entity.getStaff().getId())
                .staffName(entity.getStaff().getFullName()) // Lấy tên hiển thị
                .jobTitle(entity.getStaff().getJobTitle())  // Lấy chức vụ hiển thị
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .shiftType(entity.getShiftType())
                .note(entity.getNote())
                .isPublished(entity.isPublished())
                .build();
    }

    private ScheduleNotificationResponse mapToNotificationResponse(ScheduleNotification entity) {
        return ScheduleNotificationResponse.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .isPinned(entity.isPinned())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
