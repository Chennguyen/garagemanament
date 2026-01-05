package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    boolean existsByPhoneNumber(String phoneNumber);

    // Logic: Lấy 4 số cuối của mã NV thuộc cơ sở này để tính tăng dần
    // VD: Mã "010005" -> lấy "0005" -> ép kiểu int -> 5
    @Query("SELECT MAX(CAST(SUBSTRING(s.employeeCode, 3, 4) AS int)) FROM Staff s WHERE s.facilityCode = :facilityCode")
    Integer findMaxIdByFacility(@Param("facilityCode") String facilityCode);
}