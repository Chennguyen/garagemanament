package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.FinalInvoice;
import com.chennguyen.garagemanagement.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinalInvoiceRepository extends JpaRepository<FinalInvoice, Long> {
    Optional<FinalInvoice> findByRepairOrder(RepairOrder repairOrder);
}
