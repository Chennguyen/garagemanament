package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Quotation;
import com.chennguyen.garagemanagement.entity.RepairOrder;
import com.chennguyen.garagemanagement.entity.ServiceVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    Optional<RepairOrder> findByQuotation(Quotation quotation);

    Optional<RepairOrder> findByServiceVisit(ServiceVisit serviceVisit);
}
