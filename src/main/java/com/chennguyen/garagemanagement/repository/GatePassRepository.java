package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.FinalInvoice;
import com.chennguyen.garagemanagement.entity.GatePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatePassRepository extends JpaRepository<GatePass, Long> {
    Optional<GatePass> findByInvoice(FinalInvoice invoice);

    Optional<GatePass> findByCode(String code);
}
