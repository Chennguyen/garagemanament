package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.MaterialRequest;
import com.chennguyen.garagemanagement.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Long> {
    List<MaterialRequest> findByRepairOrderOrderByCreatedAtDesc(RepairOrder repairOrder);
}
