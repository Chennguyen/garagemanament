package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.CreateAssetRequest;
import com.chennguyen.garagemanagement.DTO.request.HandoverRequest;
import com.chennguyen.garagemanagement.DTO.request.ReportBrokenRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.AssetHandoverResponse;
import com.chennguyen.garagemanagement.DTO.response.AssetMaintenanceResponse;
import com.chennguyen.garagemanagement.DTO.response.AssetResponse;
import com.chennguyen.garagemanagement.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Controller", description = "Quản lý Tài sản & Công cụ Garage")
public class AssetController {

    final AssetService assetService;

    // ============================================================
    // QUẢN LÝ MASTER DATA
    // ============================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Thêm tài sản mới vào kho",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetResponse> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        return ApiResponse.<AssetResponse>builder()
                .result(assetService.createAsset(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy danh sách toàn bộ tài sản",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<AssetResponse>> getAllAssets() {
        return ApiResponse.<List<AssetResponse>>builder()
                .result(assetService.getAllAssets())
                .build();
    }

    @GetMapping("/{assetId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    @Operation(summary = "Xem thông tin chi tiết tài sản",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetResponse> getAsset(@PathVariable Long assetId) {
        return ApiResponse.<AssetResponse>builder()
                .result(assetService.getAssetById(assetId))
                .build();
    }

    // ============================================================
    // QUY TRÌNH BÀN GIAO
    // ============================================================

    @PostMapping("/handover")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Tạo phiếu bàn giao tài sản cho nhân viên",
            description = "Bước 1: Admin/Thủ kho chọn tài sản + nhân viên nhận. Tài sản chuyển sang PENDING_ACCEPT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetHandoverResponse> createHandover(@Valid @RequestBody HandoverRequest request) {
        return ApiResponse.<AssetHandoverResponse>builder()
                .result(assetService.createHandover(request))
                .build();
    }

    @PutMapping("/handover/{handoverId}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    @Operation(
            summary = "Nhân viên xác nhận đã nhận đồ",
            description = "Bước 2: Nhân viên bấm xác nhận nhận đồ. Tài sản chuyển sang IN_USE.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetHandoverResponse> confirmAccept(@PathVariable Long handoverId) {
        return ApiResponse.<AssetHandoverResponse>builder()
                .result(assetService.confirmAccept(handoverId))
                .build();
    }

    @PutMapping("/handover/{handoverId}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Thu hồi tài sản về kho",
            description = "Thủ kho xác nhận đã nhận lại đồ. Tài sản về AVAILABLE. Bắt buộc trước khi chốt lương cuối.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetHandoverResponse> returnAsset(@PathVariable Long handoverId) {
        return ApiResponse.<AssetHandoverResponse>builder()
                .result(assetService.returnAsset(handoverId))
                .build();
    }

    @GetMapping("/handover/staff/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Xem danh sách tài sản một nhân viên đang giữ",
            description = "Dùng khi offboarding để kiểm tra và lên danh sách thu hồi.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<AssetHandoverResponse>> getAssetsByStaff(@PathVariable String staffId) {
        return ApiResponse.<List<AssetHandoverResponse>>builder()
                .result(assetService.getAssetsByStaff(staffId))
                .build();
    }

    @GetMapping("/offboarding-check/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Kiểm tra điều kiện offboarding",
            description = "Trả về true nếu nhân viên đã trả hết đồ. Ném lỗi STAFF_STILL_HOLDING_ASSETS nếu chưa.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<Boolean> checkOffboarding(@PathVariable String staffId) {
        return ApiResponse.<Boolean>builder()
                .result(assetService.isOffboardingClean(staffId))
                .build();
    }

    // ============================================================
    // KIỂM KÊ (QR AUDIT)
    // ============================================================

    @PutMapping("/{assetId}/audit-scan")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    @Operation(
            summary = "Thợ quét QR xác nhận vẫn còn giữ tài sản",
            description = "Cập nhật lastAuditedAt và xóa cờ isAuditMissing. Dùng trong kỳ kiểm kê định kỳ.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetHandoverResponse> auditScan(@PathVariable Long assetId) {
        return ApiResponse.<AssetHandoverResponse>builder()
                .result(assetService.auditScan(assetId))
                .build();
    }

    // ============================================================
    // BÁO HỎNG & BẢO DƯỠNG
    // ============================================================

    @PostMapping("/report-broken")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    @Operation(
            summary = "Báo hỏng tài sản",
            description = "Nhân viên tạo báo cáo hỏng, kèm URL ảnh chụp. Tài sản chuyển sang BROKEN.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetMaintenanceResponse> reportBroken(@Valid @RequestBody ReportBrokenRequest request) {
        return ApiResponse.<AssetMaintenanceResponse>builder()
                .result(assetService.reportBroken(request))
                .build();
    }

    @PutMapping("/maintenance/{reportId}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Manager duyệt báo cáo hỏng",
            description = "Quyết định lỗi kỹ thuật (công ty chịu) hay lỗi người dùng (khấu trừ lương). Tài sản về AVAILABLE hoặc LIQUIDATED.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<AssetMaintenanceResponse> resolveReport(
            @PathVariable Long reportId,
            @Parameter(description = "true = Lỗi do thợ (khấu trừ lương), false = Hao mòn thường")
            @RequestParam boolean isEmployeeFault,
            @Parameter(description = "Số tiền khấu trừ (nếu lỗi do thợ)")
            @RequestParam(required = false) BigDecimal deductionAmount) {
        return ApiResponse.<AssetMaintenanceResponse>builder()
                .result(assetService.resolveMaintenanceReport(reportId, isEmployeeFault, deductionAmount))
                .build();
    }

    @GetMapping("/{assetId}/maintenance-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Lịch sử sửa chữa / bảo dưỡng của tài sản",
            description = "Xem toàn bộ lịch sử báo hỏng & sửa chữa của một thiết bị (cầu nâng, máy nén khí...).",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<AssetMaintenanceResponse>> getMaintenanceHistory(@PathVariable Long assetId) {
        return ApiResponse.<List<AssetMaintenanceResponse>>builder()
                .result(assetService.getMaintenanceHistory(assetId))
                .build();
    }

    @GetMapping("/maintenance/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Danh sách báo hỏng chờ duyệt",
            description = "Manager xem tất cả báo cáo hỏng chưa được xử lý.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<AssetMaintenanceResponse>> getPendingReports() {
        return ApiResponse.<List<AssetMaintenanceResponse>>builder()
                .result(assetService.getPendingMaintenanceReports())
                .build();
    }
}
