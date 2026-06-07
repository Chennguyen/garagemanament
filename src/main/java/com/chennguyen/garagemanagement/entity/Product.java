package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.ProductCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String sku; // Mã vạch hoặc SKU

    @Column(nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    ProductCategory category; // SPARE_PART, CONSUMABLE, MATERIAL

    String unit; // Cái, Bộ, Lít, Chai
    
    String compatibleModels; // Dòng xe tương thích (VD: Mazda 3, CX-5)

    // --- Giá trị ---
    BigDecimal costPrice;    // Giá vốn bình quân gia quyền
    BigDecimal sellingPrice; // Giá bán lẻ

    // --- Tồn kho ---
    Double minStockLevel;    // Định mức tối thiểu để cảnh báo
    Double maxStockLevel;    // Định mức tối đa
    Double currentStock;     // Tồn kho hiện tại

    @CreationTimestamp
    LocalDateTime createdAt;
    
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
