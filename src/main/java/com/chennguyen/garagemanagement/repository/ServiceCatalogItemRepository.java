package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.ServiceCatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCatalogItemRepository extends JpaRepository<ServiceCatalogItem, Long> {
    boolean existsByCode(String code);

    Optional<ServiceCatalogItem> findByCode(String code);

    List<ServiceCatalogItem> findByActiveTrue();
}
