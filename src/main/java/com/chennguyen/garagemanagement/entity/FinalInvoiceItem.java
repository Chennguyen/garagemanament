package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.QuotationItemType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "final_invoice_items")
public class FinalInvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    FinalInvoice invoice;

    @Enumerated(EnumType.STRING)
    QuotationItemType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_catalog_item_id")
    ServiceCatalogItem serviceCatalogItem;

    @Column(nullable = false)
    String description;

    Double quantity;

    @Column(precision = 19, scale = 2)
    BigDecimal unitPrice;

    @Column(precision = 19, scale = 2)
    BigDecimal totalPrice;
}
