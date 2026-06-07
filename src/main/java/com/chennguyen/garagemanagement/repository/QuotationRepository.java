package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Quotation;
import com.chennguyen.garagemanagement.entity.ServiceVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByServiceVisitOrderByCreatedAtDesc(ServiceVisit serviceVisit);
}
