package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.*;
import com.chennguyen.garagemanagement.DTO.response.*;
import com.chennguyen.garagemanagement.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Controller", description = "Quản lý Kho & Vật tư (Sản phẩm, Nhập/Xuất kho, Thẻ kho)")
public class InventoryController {

    private final InventoryService inventoryService;

    // ============================================================
    // 1. MASTER DATA (PRODUCT)
    // ============================================================

    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Thêm sản phẩm/vật tư mới", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(inventoryService.createProduct(request))
                .build();
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER')")
    @Operation(summary = "Lấy danh sách tất cả sản phẩm/vật tư", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(inventoryService.getAllProducts())
                .build();
    }

    @GetMapping("/products/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cảnh báo: Danh sách sản phẩm dưới định mức tồn kho tối thiểu", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<ProductResponse>> getLowStockProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(inventoryService.getLowStockProducts())
                .build();
    }

    // ============================================================
    // 2. NHẬP KHO (INBOUND)
    // ============================================================

    @PostMapping("/purchase-orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Tạo Đơn đặt hàng (Purchase Order)", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<PurchaseOrderResponse> createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        return ApiResponse.<PurchaseOrderResponse>builder()
                .result(inventoryService.createPurchaseOrder(request))
                .build();
    }

    @PutMapping("/purchase-orders/{poId}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Nhập kho (Goods Receipt) từ Đơn đặt hàng",
            description = "Điền số lượng thực nhận. Hệ thống sẽ tự cập nhật tồn kho, tính lại giá vốn BQGQ và ghi Thẻ kho.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<PurchaseOrderResponse> receivePurchaseOrder(
            @PathVariable Long poId,
            @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        return ApiResponse.<PurchaseOrderResponse>builder()
                .result(inventoryService.receivePurchaseOrder(poId, request))
                .build();
    }

    // ============================================================
    // 3. XUẤT KHO (OUTBOUND)
    // ============================================================

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC')")
    @Operation(
            summary = "Xuất kho (Service Order / Retail / Internal)",
            description = "Trừ tồn kho và ghi log vào Thẻ kho. (Dùng khi Thợ nhận đồ sửa xe hoặc bán lẻ).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<String> issueMaterial(@Valid @RequestBody IssueMaterialRequest request) {
        inventoryService.issueMaterial(request);
        return ApiResponse.<String>builder()
                .result("Xuất kho thành công!")
                .build();
    }

    // ============================================================
    // 4. BÁO CÁO (REPORTS)
    // ============================================================

    @GetMapping("/products/{productId}/stock-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Xem Thẻ kho (Stock Card) của một sản phẩm",
            description = "Liệt kê toàn bộ lịch sử xuất/nhập/điều chỉnh kho của 1 mã hàng.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<InventoryTransactionResponse>> getStockCard(@PathVariable Long productId) {
        return ApiResponse.<List<InventoryTransactionResponse>>builder()
                .result(inventoryService.getStockCard(productId))
                .build();
    }
}
