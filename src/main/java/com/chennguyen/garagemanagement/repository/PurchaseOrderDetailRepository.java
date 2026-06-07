package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.PurchaseOrder;
import com.chennguyen.garagemanagement.entity.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
    List<PurchaseOrderDetail> findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
