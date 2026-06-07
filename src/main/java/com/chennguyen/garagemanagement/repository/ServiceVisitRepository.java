package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.ServiceVisit;
import com.chennguyen.garagemanagement.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceVisitRepository extends JpaRepository<ServiceVisit, Long> {
    List<ServiceVisit> findByVehicleOrderByCheckInAtDesc(Vehicle vehicle);
}
