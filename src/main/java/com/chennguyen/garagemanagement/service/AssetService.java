package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.CreateAssetRequest;
import com.chennguyen.garagemanagement.DTO.request.HandoverRequest;
import com.chennguyen.garagemanagement.DTO.request.ReportBrokenRequest;
import com.chennguyen.garagemanagement.DTO.response.AssetHandoverResponse;
import com.chennguyen.garagemanagement.DTO.response.AssetMaintenanceResponse;
import com.chennguyen.garagemanagement.DTO.response.AssetResponse;
import com.chennguyen.garagemanagement.emuns.AssetStatus;
import com.chennguyen.garagemanagement.emuns.HandoverStatus;
import com.chennguyen.garagemanagement.entity.Asset;
import com.chennguyen.garagemanagement.entity.AssetHandover;
import com.chennguyen.garagemanagement.entity.AssetMaintenance;
import com.chennguyen.garagemanagement.entity.Staff;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.AssetHandoverRepository;
import com.chennguyen.garagemanagement.repository.AssetMaintenanceRepository;
import com.chennguyen.garagemanagement.repository.AssetRepository;
import com.chennguyen.garagemanagement.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetHandoverRepository handoverRepository;
    private final AssetMaintenanceRepository maintenanceRepository;
    private final StaffRepository staffRepository;

    // ============================================================
    // 1. QUẢN LÝ TÀI SẢN (ASSET MASTER DATA)
    // ============================================================

    /** Thêm tài sản mới vào kho */
    @Transactional
    public AssetResponse createAsset(CreateAssetRequest request) {
        if (assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new AppException(ErrorCode.ASSET_CODE_EXISTED);
        }

        Asset asset = Asset.builder()
                .assetCode(request.getAssetCode())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .location(request.getLocation())
                .status(AssetStatus.AVAILABLE)
                .purchasePrice(request.getPurchasePrice())
                .currentValue(request.getCurrentValue() != null ? request.getCurrentValue() : request.getPurchasePrice())
                .purchaseDate(request.getPurchaseDate())
                .nextMaintenanceDate(request.getNextMaintenanceDate())
                .maintenanceNote(request.getMaintenanceNote())
                .build();

        Asset saved = assetRepository.save(asset);
        log.info("Asset created: {} - {}", saved.getAssetCode(), saved.getName());
        return toAssetResponse(saved);
    }

    /** Lấy thông tin tài sản theo ID */
    public AssetResponse getAssetById(Long assetId) {
        Asset asset = findAssetOrThrow(assetId);
        return toAssetResponse(asset);
    }

    /** Lấy danh sách tất cả tài sản */
    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(this::toAssetResponse)
                .toList();
    }

    // ============================================================
    // 2. CẤP PHÁT & BÀN GIAO (HANDOVER PROCESS)
    // ============================================================

    /**
     * BƯỚC 1 - Admin/Thủ kho: Tạo phiếu bàn giao, giao tài sản cho nhân viên.
     * Tài sản chuyển sang PENDING_ACCEPT, chờ thợ bấm xác nhận.
     */
    @Transactional
    public AssetHandoverResponse createHandover(HandoverRequest request) {
        // Lấy thông tin tài sản
        Asset asset = findAssetOrThrow(request.getAssetId());

        // Chỉ tài sản đang AVAILABLE mới được cấp phát
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new AppException(ErrorCode.ASSET_NOT_AVAILABLE);
        }

        // Lấy nhân viên nhận đồ
        Staff receiver = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Lấy người đang thực hiện thao tác (Admin/Thủ kho)
        Staff admin = getCurrentAuthenticatedStaff();

        // Tạo phiếu bàn giao
        AssetHandover handover = AssetHandover.builder()
                .asset(asset)
                .staff(receiver)
                .handedOverBy(admin)
                .status(HandoverStatus.PENDING)
                .note(request.getNote())
                .build();

        handoverRepository.save(handover);

        // Cập nhật trạng thái tài sản -> PENDING_ACCEPT
        asset.setStatus(AssetStatus.PENDING_ACCEPT);
        assetRepository.save(asset);

        log.info("Handover created: Asset [{}] -> Staff [{}]", asset.getAssetCode(), receiver.getEmployeeCode());
        return toHandoverResponse(handover);
    }

    /**
     * BƯỚC 2 - Nhân viên: Xác nhận đã nhận đồ.
     * Tài sản chuyển sang IN_USE, ghi nhận currentHolder.
     */
    @Transactional
    public AssetHandoverResponse confirmAccept(Long handoverId) {
        AssetHandover handover = findHandoverOrThrow(handoverId);

        if (handover.getStatus() != HandoverStatus.PENDING) {
            throw new AppException(ErrorCode.ASSET_ALREADY_ACCEPTED);
        }

        // Cập nhật phiếu
        handover.setStatus(HandoverStatus.ACCEPTED);
        handover.setAcceptedAt(LocalDateTime.now());
        handoverRepository.save(handover);

        // Cập nhật tài sản -> IN_USE + ghi nhận người giữ
        Asset asset = handover.getAsset();
        asset.setStatus(AssetStatus.IN_USE);
        asset.setCurrentHolder(handover.getStaff());
        assetRepository.save(asset);

        log.info("Asset [{}] accepted by staff [{}]", asset.getAssetCode(), handover.getStaff().getEmployeeCode());
        return toHandoverResponse(handover);
    }

    /**
     * Thu hồi tài sản - Thủ kho xác nhận đã nhận lại đồ từ nhân viên.
     * Tài sản về AVAILABLE.
     */
    @Transactional
    public AssetHandoverResponse returnAsset(Long handoverId) {
        AssetHandover handover = findHandoverOrThrow(handoverId);

        if (handover.getStatus() != HandoverStatus.ACCEPTED) {
            throw new AppException(ErrorCode.HANDOVER_NOT_FOUND);
        }

        // Cập nhật phiếu
        handover.setStatus(HandoverStatus.RETURNED);
        handover.setReturnedAt(LocalDateTime.now());
        handoverRepository.save(handover);

        // Trả tài sản về kho
        Asset asset = handover.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setCurrentHolder(null);
        assetRepository.save(asset);

        log.info("Asset [{}] returned to warehouse by staff [{}]", asset.getAssetCode(), handover.getStaff().getEmployeeCode());
        return toHandoverResponse(handover);
    }

    /**
     * Lấy danh sách tài sản nhân viên đang giữ.
     * Dùng khi offboarding để hiện cảnh báo.
     */
    public List<AssetHandoverResponse> getAssetsByStaff(String staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return handoverRepository.findByStaffAndStatus(staff, HandoverStatus.ACCEPTED)
                .stream().map(this::toHandoverResponse).toList();
    }

    /**
     * Kiểm tra offboarding: nhân viên còn giữ đồ không?
     * Trả về true nếu CLEAN (không còn giữ gì), false nếu còn giữ đồ.
     */
    public boolean isOffboardingClean(String staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean stillHolding = handoverRepository.existsByStaffAndStatus(staff, HandoverStatus.ACCEPTED);
        if (stillHolding) {
            throw new AppException(ErrorCode.STAFF_STILL_HOLDING_ASSETS);
        }
        return true;
    }

    // ============================================================
    // 3. KIỂM KÊ (QR AUDIT)
    // ============================================================

    /**
     * Thợ quét mã QR để xác nhận "vẫn còn giữ" trong kỳ kiểm kê.
     * Cập nhật lastAuditedAt và xóa cờ thất lạc.
     */
    @Transactional
    public AssetHandoverResponse auditScan(Long assetId) {
        Asset asset = findAssetOrThrow(assetId);
        Staff currentStaff = getCurrentAuthenticatedStaff();

        // Tìm phiếu đang active của nhân viên này với tài sản này
        AssetHandover handover = handoverRepository.findByAssetAndStatus(asset, HandoverStatus.ACCEPTED)
                .orElseThrow(() -> new AppException(ErrorCode.HANDOVER_NOT_FOUND));

        // Xác nhận đúng người đang giữ mới được quét
        if (!handover.getStaff().getId().equals(currentStaff.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        handover.setLastAuditedAt(LocalDateTime.now());
        handover.setAuditMissing(false);
        handoverRepository.save(handover);

        log.info("Audit scan OK: Asset [{}] confirmed by [{}]", asset.getAssetCode(), currentStaff.getEmployeeCode());
        return toHandoverResponse(handover);
    }

    // ============================================================
    // 4. BÁO HỎNG / BẢO DƯỠNG (MAINTENANCE)
    // ============================================================

    /**
     * Nhân viên tạo báo cáo hỏng - kèm ảnh chụp.
     * Tài sản chuyển sang BROKEN chờ Manager duyệt.
     */
    @Transactional
    public AssetMaintenanceResponse reportBroken(ReportBrokenRequest request) {
        Asset asset = findAssetOrThrow(request.getAssetId());
        Staff reporter = getCurrentAuthenticatedStaff();

        AssetMaintenance report = AssetMaintenance.builder()
                .asset(asset)
                .reportedBy(reporter)
                .issueDescription(request.getIssueDescription())
                .imageUrl(request.getImageUrl())
                .isResolved(false)
                .isEmployeeFault(false)
                .build();

        maintenanceRepository.save(report);

        // Đánh dấu tài sản BROKEN
        asset.setStatus(AssetStatus.BROKEN);
        assetRepository.save(asset);

        log.info("Broken report created for asset [{}] by [{}]", asset.getAssetCode(), reporter.getEmployeeCode());
        return toMaintenanceResponse(report);
    }

    /**
     * Manager duyệt báo cáo hỏng:
     * - isEmployeeFault = false -> Công ty sửa, tài sản về AVAILABLE
     * - isEmployeeFault = true -> Ghi nhận khấu trừ lương, tài sản về AVAILABLE (hoặc LIQUIDATED nếu mất)
     */
    @Transactional
    public AssetMaintenanceResponse resolveMaintenanceReport(Long reportId, boolean isEmployeeFault,
                                                              java.math.BigDecimal deductionAmount) {
        AssetMaintenance report = maintenanceRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        report.setResolved(true);
        report.setEmployeeFault(isEmployeeFault);
        report.setDeductionAmount(deductionAmount);
        report.setResolvedAt(LocalDateTime.now());
        maintenanceRepository.save(report);

        // Cập nhật tài sản
        Asset asset = report.getAsset();
        if (isEmployeeFault && deductionAmount != null && deductionAmount.compareTo(asset.getCurrentValue()) >= 0) {
            // Mất hoặc hỏng hoàn toàn -> Thanh lý
            asset.setStatus(AssetStatus.LIQUIDATED);
            asset.setCurrentHolder(null);
        } else {
            // Đã sửa xong -> Về kho
            asset.setStatus(AssetStatus.AVAILABLE);
            asset.setCurrentHolder(null);
        }
        assetRepository.save(asset);

        log.info("Maintenance report [{}] resolved. Employee fault: {}, Deduction: {}", reportId, isEmployeeFault, deductionAmount);
        return toMaintenanceResponse(report);
    }

    /** Lấy lịch sử bảo dưỡng của một tài sản */
    public List<AssetMaintenanceResponse> getMaintenanceHistory(Long assetId) {
        Asset asset = findAssetOrThrow(assetId);
        return maintenanceRepository.findByAssetOrderByReportedAtDesc(asset)
                .stream().map(this::toMaintenanceResponse).toList();
    }

    /** Lấy danh sách báo cáo chưa xử lý (Dành cho Manager) */
    public List<AssetMaintenanceResponse> getPendingMaintenanceReports() {
        return maintenanceRepository.findByIsResolvedFalse()
                .stream().map(this::toMaintenanceResponse).toList();
    }

    // ============================================================
    // HELPER: Lấy Staff đang đăng nhập từ Token
    // ============================================================

    private Staff getCurrentAuthenticatedStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = authentication.getName();
        return staffRepository.findByEmployeeCode(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private Asset findAssetOrThrow(Long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));
    }

    private AssetHandover findHandoverOrThrow(Long handoverId) {
        return handoverRepository.findById(handoverId)
                .orElseThrow(() -> new AppException(ErrorCode.HANDOVER_NOT_FOUND));
    }

    // ============================================================
    // HELPER: Map Entity -> Response DTO
    // ============================================================

    private AssetResponse toAssetResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .assetCode(asset.getAssetCode())
                .name(asset.getName())
                .description(asset.getDescription())
                .category(asset.getCategory())
                .location(asset.getLocation())
                .status(asset.getStatus())
                .currentHolderName(asset.getCurrentHolder() != null ? asset.getCurrentHolder().getFullName() : null)
                .currentHolderCode(asset.getCurrentHolder() != null ? asset.getCurrentHolder().getEmployeeCode() : null)
                .purchasePrice(asset.getPurchasePrice())
                .currentValue(asset.getCurrentValue())
                .purchaseDate(asset.getPurchaseDate())
                .lastMaintenanceDate(asset.getLastMaintenanceDate())
                .nextMaintenanceDate(asset.getNextMaintenanceDate())
                .maintenanceNote(asset.getMaintenanceNote())
                .build();
    }

    private AssetHandoverResponse toHandoverResponse(AssetHandover h) {
        return AssetHandoverResponse.builder()
                .id(h.getId())
                .assetId(h.getAsset().getId())
                .assetCode(h.getAsset().getAssetCode())
                .assetName(h.getAsset().getName())
                .staffId(h.getStaff().getId())
                .staffName(h.getStaff().getFullName())
                .employeeCode(h.getStaff().getEmployeeCode())
                .handedOverByName(h.getHandedOverBy() != null ? h.getHandedOverBy().getFullName() : null)
                .status(h.getStatus())
                .assignedAt(h.getAssignedAt())
                .acceptedAt(h.getAcceptedAt())
                .returnedAt(h.getReturnedAt())
                .lastAuditedAt(h.getLastAuditedAt())
                .isAuditMissing(h.isAuditMissing())
                .note(h.getNote())
                .build();
    }

    private AssetMaintenanceResponse toMaintenanceResponse(AssetMaintenance m) {
        return AssetMaintenanceResponse.builder()
                .id(m.getId())
                .assetId(m.getAsset().getId())
                .assetCode(m.getAsset().getAssetCode())
                .assetName(m.getAsset().getName())
                .reportedByName(m.getReportedBy() != null ? m.getReportedBy().getFullName() : null)
                .reportedByCode(m.getReportedBy() != null ? m.getReportedBy().getEmployeeCode() : null)
                .issueDescription(m.getIssueDescription())
                .imageUrl(m.getImageUrl())
                .isResolved(m.isResolved())
                .isEmployeeFault(m.isEmployeeFault())
                .deductionAmount(m.getDeductionAmount())
                .reportedAt(m.getReportedAt())
                .resolvedAt(m.getResolvedAt())
                .build();
    }
}
