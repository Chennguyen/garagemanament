package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.FinalInvoice;
import com.chennguyen.garagemanagement.entity.FinalInvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalInvoiceItemRepository extends JpaRepository<FinalInvoiceItem, Long> {
    List<FinalInvoiceItem> findByInvoice(FinalInvoice invoice);
}
