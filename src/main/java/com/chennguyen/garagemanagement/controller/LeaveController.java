package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.CreateLeaveRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.LeaveResponse;
import com.chennguyen.garagemanagement.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Controller", description = "Quản lý Đơn xin nghỉ phép & Phê duyệt (Có đồng bộ với Lịch làm việc)")
public class LeaveController {

    private final LeaveService leaveService;

    // 1. Tạo đơn nghỉ (Nhân viên)
    @PostMapping
    @Operation(
            summary = "Nộp đơn xin nghỉ phép",
            description = "Nhân viên tạo yêu cầu nghỉ phép (Ốm, Việc riêng...). Trạng thái ban đầu sẽ là PENDING chờ duyệt.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<LeaveResponse> createRequest(@RequestBody CreateLeaveRequest request) {
        return ApiResponse.<LeaveResponse>builder()
                .result(leaveService.createLeaveRequest(request))
                .build();
    }

    // 2. Duyệt đơn (Manager) -> Trigger Sync
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Duyệt đơn nghỉ phép (Approve)",
            description = "QUAN TRỌNG: Khi Admin/Manager duyệt đơn, hệ thống sẽ tự động cập nhật Lịch làm việc (Schedule): Gỡ ca trực của nhân viên trong ngày đó hoặc đánh dấu là nghỉ.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<LeaveResponse> approveRequest(
            @Parameter(description = "ID của đơn xin nghỉ") @PathVariable Long id) {
        return ApiResponse.<LeaveResponse>builder()
                .result(leaveService.approveLeave(id))
                .message("Đã duyệt đơn & Đồng bộ sang lịch làm việc!")
                .build();
    }

    // 3. Từ chối đơn (Manager)
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Từ chối đơn nghỉ phép (Reject)",
            description = "Admin/Manager từ chối đơn kèm theo lý do. Trạng thái đơn chuyển sang REJECTED.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<LeaveResponse> rejectRequest(
            @Parameter(description = "ID của đơn xin nghỉ") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Lý do từ chối (Gửi dạng text/plain)")
            @RequestBody String reason) {
        return ApiResponse.<LeaveResponse>builder()
                .result(leaveService.rejectLeave(id, reason))
                .build();
    }
}