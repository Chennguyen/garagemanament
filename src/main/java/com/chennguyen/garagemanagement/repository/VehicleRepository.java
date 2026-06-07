package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    boolean existsByVin(String vin);

    Optional<Vehicle> findByVin(String vin);
}
