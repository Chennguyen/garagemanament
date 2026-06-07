package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.MaterialRequest;
import com.chennguyen.garagemanagement.entity.MaterialRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRequestItemRepository extends JpaRepository<MaterialRequestItem, Long> {
    List<MaterialRequestItem> findByMaterialRequest(MaterialRequest materialRequest);
}
