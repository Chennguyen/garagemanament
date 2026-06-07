package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.emuns.HandoverStatus;
import com.chennguyen.garagemanagement.entity.Asset;
import com.chennguyen.garagemanagement.entity.AssetHandover;
import com.chennguyen.garagemanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetHandoverRepository extends JpaRepository<AssetHandover, Long> {

    // Check xem thợ còn cầm đồ nào không (Dùng cho OFFBOARDING)
    boolean existsByStaffAndStatus(Staff staff, HandoverStatus status);

    // Lấy danh sách tài sản đang được một thợ giữ (Danh sách cần thu hồi)
    List<AssetHandover> findByStaffAndStatus(Staff staff, HandoverStatus status);

    // Lấy phiếu bàn giao active của một tài sản cụ thể
    Optional<AssetHandover> findByAssetAndStatus(Asset asset, HandoverStatus status);

    // Đánh dấu thất lạc cho các phiếu chưa kiểm kê trong chu kỳ
    @Modifying
    @Query("UPDATE AssetHandover h SET h.isAuditMissing = true WHERE h.status = 'ACCEPTED' AND h.lastAuditedAt IS NULL")
    void markAllUnauditedAsMissing();
}