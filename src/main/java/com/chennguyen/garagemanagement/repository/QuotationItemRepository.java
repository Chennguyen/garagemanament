package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Quotation;
import com.chennguyen.garagemanagement.entity.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationItemRepository extends JpaRepository<QuotationItem, Long> {
    List<QuotationItem> findByQuotation(Quotation quotation);
}
