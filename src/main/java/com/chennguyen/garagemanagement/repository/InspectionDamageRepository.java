package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.InspectionDamage;
import com.chennguyen.garagemanagement.entity.ServiceVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionDamageRepository extends JpaRepository<InspectionDamage, Long> {
    List<InspectionDamage> findByServiceVisit(ServiceVisit serviceVisit);
}
