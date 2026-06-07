package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.InventoryTransaction;
import com.chennguyen.garagemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    // Lấy Thẻ kho (Stock Card) của 1 sản phẩm
    List<InventoryTransaction> findByProductOrderByTransactionDateDesc(Product product);
}
