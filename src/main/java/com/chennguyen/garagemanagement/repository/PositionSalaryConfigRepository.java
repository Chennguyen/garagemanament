package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.emuns.EmployeeType;
import com.chennguyen.garagemanagement.entity.PositionSalaryConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionSalaryConfigRepository extends JpaRepository<PositionSalaryConfig, Long> {

    // 👇 SỬA: Thay Position (Enum) thành String jobTitle để khớp với Entity Staff
    Optional<PositionSalaryConfig> findByJobTitleAndEmployeeType(String jobTitle, EmployeeType employeeType);
}
