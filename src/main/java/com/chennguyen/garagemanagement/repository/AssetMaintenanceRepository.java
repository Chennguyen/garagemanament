package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Asset;
import com.chennguyen.garagemanagement.entity.AssetMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetMaintenanceRepository extends JpaRepository<AssetMaintenance, Long> {

    // Lịch sử báo hỏng/bảo dưỡng của một tài sản (cho tab "Lịch sử sửa chữa")
    List<AssetMaintenance> findByAssetOrderByReportedAtDesc(Asset asset);

    // Các báo cáo chưa được xử lý (Manager xem để duyệt)
    List<AssetMaintenance> findByIsResolvedFalse();

    // Báo cáo chưa xử lý của một tài sản cụ thể
    List<AssetMaintenance> findByAssetAndIsResolvedFalse(Asset asset);
}
