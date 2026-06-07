package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.*;
import com.chennguyen.garagemanagement.DTO.response.*;
import com.chennguyen.garagemanagement.emuns.PurchaseOrderStatus;
import com.chennguyen.garagemanagement.emuns.TransactionType;
import com.chennguyen.garagemanagement.entity.*;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository poRepository;
    private final PurchaseOrderDetailRepository poDetailRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final StaffRepository staffRepository;

    // ============================================================
    // 1. MASTER DATA (PRODUCT)
    // ============================================================

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new AppException(ErrorCode.PRODUCT_SKU_EXISTED);
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .category(request.getCategory())
                .unit(request.getUnit())
                .compatibleModels(request.getCompatibleModels())
                .costPrice(BigDecimal.ZERO)
                .sellingPrice(request.getSellingPrice() != null ? request.getSellingPrice() : BigDecimal.ZERO)
                .minStockLevel(request.getMinStockLevel() != null ? request.getMinStockLevel() : 0.0)
                .maxStockLevel(request.getMaxStockLevel() != null ? request.getMaxStockLevel() : 9999.0)
                .currentStock(0.0)
                .build();

        Product saved = productRepository.save(product);
        return toProductResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::toProductResponse).toList();
    }

    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findProductsBelowMinStock().stream().map(this::toProductResponse).toList();
    }

    // ============================================================
    // 2. NHẬP KHO (INBOUND)
    // ============================================================

    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        PurchaseOrder po = PurchaseOrder.builder()
                .supplier(supplier)
                .orderDate(java.time.LocalDate.now())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .status(PurchaseOrderStatus.ORDERED)
                .note(request.getNote())
                .totalAmount(BigDecimal.ZERO)
                .build();

        po = poRepository.save(po);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderDetail> details = new ArrayList<>();

        for (PurchaseOrderDetailRequest detailReq : request.getDetails()) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            BigDecimal detailTotal = detailReq.getUnitPrice().multiply(BigDecimal.valueOf(detailReq.getOrderedQuantity()));
            totalAmount = totalAmount.add(detailTotal);

            PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .orderedQuantity(detailReq.getOrderedQuantity())
                    .receivedQuantity(0.0) // Chưa nhận
                    .unitPrice(detailReq.getUnitPrice())
                    .totalPrice(detailTotal)
                    .build();

            details.add(poDetailRepository.save(detail));
        }

        po.setTotalAmount(totalAmount);
        poRepository.save(po);

        return toPurchaseOrderResponse(po, details);
    }

    @Transactional
    public PurchaseOrderResponse receivePurchaseOrder(Long poId, ReceivePurchaseOrderRequest request) {
        PurchaseOrder po = poRepository.findById(poId)
                .orElseThrow(() -> new AppException(ErrorCode.PO_NOT_FOUND));

        if (po.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new AppException(ErrorCode.PO_ALREADY_RECEIVED);
        }

        Staff receiver = getCurrentAuthenticatedStaff();
        List<PurchaseOrderDetail> details = poDetailRepository.findByPurchaseOrder(po);
        Map<Long, Double> receivedMap = request.getReceivedQuantities();

        for (PurchaseOrderDetail detail : details) {
            Double receivedQty = receivedMap.getOrDefault(detail.getId(), 0.0);
            if (receivedQty > 0) {
                detail.setReceivedQuantity(receivedQty);
                poDetailRepository.save(detail);

                Product product = detail.getProduct();

                // Tính toán giá vốn bình quân gia quyền (Weighted Average Cost)
                // Cost = (Tồn cũ * Giá cũ + Nhập mới * Giá mới) / (Tồn cũ + Nhập mới)
                BigDecimal currentTotalValue = product.getCostPrice().multiply(BigDecimal.valueOf(product.getCurrentStock()));
                BigDecimal newReceivedValue = detail.getUnitPrice().multiply(BigDecimal.valueOf(receivedQty));
                Double newTotalStock = product.getCurrentStock() + receivedQty;

                BigDecimal newAvgCost = currentTotalValue.add(newReceivedValue)
                        .divide(BigDecimal.valueOf(newTotalStock), 2, RoundingMode.HALF_UP);

                product.setCostPrice(newAvgCost);
                product.setCurrentStock(newTotalStock);
                productRepository.save(product);

                // Ghi log Thẻ kho
                InventoryTransaction transaction = InventoryTransaction.builder()
                        .product(product)
                        .type(TransactionType.INBOUND_PURCHASE)
                        .quantity(receivedQty) // Số dương (Nhập kho)
                        .stockAfterTransaction(newTotalStock)
                        .referenceId("PO-" + po.getId())
                        .note("Nhập kho từ PO")
                        .performedBy(receiver)
                        .build();

                transactionRepository.save(transaction);
            }
        }

        po.setStatus(PurchaseOrderStatus.RECEIVED);
        poRepository.save(po);

        return toPurchaseOrderResponse(po, details);
    }

    // ============================================================
    // 3. XUẤT KHO (OUTBOUND)
    // ============================================================

    @Transactional
    public void issueMaterial(IssueMaterialRequest request) {
        Staff issuer = getCurrentAuthenticatedStaff();
        Map<Long, Double> issueMap = request.getIssuedQuantities();

        for (Map.Entry<Long, Double> entry : issueMap.entrySet()) {
            Long productId = entry.getKey();
            Double issueQty = entry.getValue();

            if (issueQty <= 0) continue;

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getCurrentStock() < issueQty) {
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
            }

            // Trừ tồn kho
            Double newStock = product.getCurrentStock() - issueQty;
            product.setCurrentStock(newStock);
            productRepository.save(product);

            // Ghi log Thẻ kho
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .product(product)
                    .type(request.getTransactionType())
                    .quantity(-issueQty) // Số âm (Xuất kho)
                    .stockAfterTransaction(newStock)
                    .referenceId(request.getReferenceId())
                    .note(request.getNote())
                    .performedBy(issuer)
                    .build();

            transactionRepository.save(transaction);
        }
    }

    // ============================================================
    // 4. BÁO CÁO / THẺ KHO (STOCK CARD)
    // ============================================================

    public List<InventoryTransactionResponse> getStockCard(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return transactionRepository.findByProductOrderByTransactionDateDesc(product)
                .stream().map(this::toTransactionResponse).toList();
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private Staff getCurrentAuthenticatedStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = authentication.getName();
        return staffRepository.findByEmployeeCode(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private ProductResponse toProductResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .category(p.getCategory())
                .unit(p.getUnit())
                .compatibleModels(p.getCompatibleModels())
                .costPrice(p.getCostPrice())
                .sellingPrice(p.getSellingPrice())
                .minStockLevel(p.getMinStockLevel())
                .maxStockLevel(p.getMaxStockLevel())
                .currentStock(p.getCurrentStock())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrder po, List<PurchaseOrderDetail> details) {
        List<PurchaseOrderResponse.Detail> detailResponses = details.stream().map(d ->
                PurchaseOrderResponse.Detail.builder()
                        .id(d.getId())
                        .productId(d.getProduct().getId())
                        .productName(d.getProduct().getName())
                        .sku(d.getProduct().getSku())
                        .orderedQuantity(d.getOrderedQuantity())
                        .receivedQuantity(d.getReceivedQuantity())
                        .unitPrice(d.getUnitPrice())
                        .totalPrice(d.getTotalPrice())
                        .build()
        ).collect(Collectors.toList());

        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .supplierId(po.getSupplier() != null ? po.getSupplier().getId() : null)
                .supplierName(po.getSupplier() != null ? po.getSupplier().getName() : null)
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .status(po.getStatus())
                .totalAmount(po.getTotalAmount())
                .note(po.getNote())
                .details(detailResponses)
                .build();
    }

    private InventoryTransactionResponse toTransactionResponse(InventoryTransaction t) {
        return InventoryTransactionResponse.builder()
                .id(t.getId())
                .productId(t.getProduct().getId())
                .productName(t.getProduct().getName())
                .sku(t.getProduct().getSku())
                .type(t.getType())
                .quantity(t.getQuantity())
                .stockAfterTransaction(t.getStockAfterTransaction())
                .referenceId(t.getReferenceId())
                .note(t.getNote())
                .performedByName(t.getPerformedBy() != null ? t.getPerformedBy().getFullName() : null)
                .transactionDate(t.getTransactionDate())
                .build();
    }
}
