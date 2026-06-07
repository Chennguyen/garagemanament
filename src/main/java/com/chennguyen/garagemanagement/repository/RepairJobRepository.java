package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.RepairJob;
import com.chennguyen.garagemanagement.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairJobRepository extends JpaRepository<RepairJob, Long> {
    List<RepairJob> findByRepairOrder(RepairOrder repairOrder);
}
