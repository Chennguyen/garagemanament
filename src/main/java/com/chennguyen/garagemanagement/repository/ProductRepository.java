package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);

    // Cảnh báo tồn kho dưới mức tối thiểu
    @Query("SELECT p FROM Product p WHERE p.currentStock < p.minStockLevel")
    List<Product> findProductsBelowMinStock();
}
