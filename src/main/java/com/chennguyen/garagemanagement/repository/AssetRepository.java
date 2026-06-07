package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    boolean existsByAssetCode(String assetCode);
}