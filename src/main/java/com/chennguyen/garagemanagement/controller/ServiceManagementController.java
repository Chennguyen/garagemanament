package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.*;
import com.chennguyen.garagemanagement.DTO.response.*;
import com.chennguyen.garagemanagement.service.ServiceManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-management")
@RequiredArgsConstructor
@Tag(name = "Service Management", description = "Vehicle CRM, reception, quotation, repair order, QC, wash, billing and gate pass")
public class ServiceManagementController {
    private final ServiceManagementService serviceManagementService;

    @PostMapping("/vehicles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR')")
    @Operation(summary = "Create or update vehicle profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<VehicleResponse> upsertVehicle(@Valid @RequestBody UpsertVehicleRequest request) {
        return ApiResponse.<VehicleResponse>builder()
                .result(serviceManagementService.upsertVehicle(request))
                .build();
    }

    @GetMapping("/vehicles/{licensePlate}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR', 'FOREMAN', 'MECHANIC', 'CASHIER')")
    @Operation(summary = "Get vehicle profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<VehicleResponse> getVehicle(@PathVariable String licensePlate) {
        return ApiResponse.<VehicleResponse>builder()
                .result(serviceManagementService.getVehicle(licensePlate))
                .build();
    }

    @GetMapping("/vehicles/{licensePlate}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR', 'FOREMAN', 'MECHANIC', 'CASHIER')")
    @Operation(summary = "Get vehicle service history", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<VehicleHistoryResponse> getVehicleHistory(@PathVariable String licensePlate) {
        return ApiResponse.<VehicleHistoryResponse>builder()
                .result(serviceManagementService.getVehicleHistory(licensePlate))
                .build();
    }

    @PostMapping("/service-catalog")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create labor/service price list item", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<ServiceCatalogItemResponse> createServiceCatalogItem(
            @Valid @RequestBody CreateServiceCatalogItemRequest request) {
        return ApiResponse.<ServiceCatalogItemResponse>builder()
                .result(serviceManagementService.createServiceCatalogItem(request))
                .build();
    }

    @GetMapping("/service-catalog")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR', 'FOREMAN', 'CASHIER')")
    @Operation(summary = "Get active labor/service price list", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<ServiceCatalogItemResponse>> getActiveServiceCatalogItems() {
        return ApiResponse.<List<ServiceCatalogItemResponse>>builder()
                .result(serviceManagementService.getActiveServiceCatalogItems())
                .build();
    }

    @PostMapping("/visits/check-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR')")
    @Operation(summary = "Reception check-in with odo, fuel, inspection damages and customer request", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<ServiceVisitResponse> checkIn(@Valid @RequestBody CheckInServiceVisitRequest request) {
        return ApiResponse.<ServiceVisitResponse>builder()
                .result(serviceManagementService.checkIn(request))
                .build();
    }

    @GetMapping("/visits/{visitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR', 'FOREMAN', 'MECHANIC', 'CASHIER')")
    @Operation(summary = "Get service visit detail", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<ServiceVisitResponse> getServiceVisit(@PathVariable Long visitId) {
        return ApiResponse.<ServiceVisitResponse>builder()
                .result(serviceManagementService.getServiceVisit(visitId))
                .build();
    }

    @PostMapping("/quotations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR')")
    @Operation(summary = "Create draft quotation with parts, labor and other fees", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<QuotationResponse> createQuotation(@Valid @RequestBody CreateQuotationRequest request) {
        return ApiResponse.<QuotationResponse>builder()
                .result(serviceManagementService.createQuotation(request))
                .build();
    }

    @PutMapping("/quotations/{quotationId}/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR')")
    @Operation(summary = "Mark quotation as sent to customer", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<QuotationResponse> sendQuotation(@PathVariable Long quotationId) {
        return ApiResponse.<QuotationResponse>builder()
                .result(serviceManagementService.sendQuotation(quotationId))
                .build();
    }

    @PutMapping("/quotations/{quotationId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR')")
    @Operation(summary = "Approve quotation and auto-create repair order plus material request", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<RepairOrderResponse> approveQuotation(@PathVariable Long quotationId) {
        return ApiResponse.<RepairOrderResponse>builder()
                .result(serviceManagementService.approveQuotation(quotationId))
                .build();
    }

    @PutMapping("/quotations/{quotationId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR')")
    @Operation(summary = "Reject quotation", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<QuotationResponse> rejectQuotation(
            @PathVariable Long quotationId,
            @RequestBody(required = false) RejectQuotationRequest request) {
        return ApiResponse.<QuotationResponse>builder()
                .result(serviceManagementService.rejectQuotation(quotationId, request))
                .build();
    }

    @GetMapping("/repair-orders/{repairOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SERVICE_ADVISOR', 'FOREMAN', 'MECHANIC', 'STOREKEEPER', 'CASHIER')")
    @Operation(summary = "Get repair order detail", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<RepairOrderResponse> getRepairOrder(@PathVariable Long repairOrderId) {
        return ApiResponse.<RepairOrderResponse>builder()
                .result(serviceManagementService.getRepairOrder(repairOrderId))
                .build();
    }

    @PutMapping("/repair-jobs/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FOREMAN')")
    @Operation(summary = "Assign mechanic to one repair job", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<RepairJobResponse> assignRepairJob(@Valid @RequestBody AssignRepairJobRequest request) {
        return ApiResponse.<RepairJobResponse>builder()
                .result(serviceManagementService.assignRepairJob(request))
                .build();
    }

    @PutMapping("/repair-jobs/{jobId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FOREMAN', 'MECHANIC')")
    @Operation(summary = "Mechanic updates job status in real time", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<RepairJobResponse> updateRepairJobStatus(
            @PathVariable Long jobId,
            @Valid @RequestBody UpdateRepairJobStatusRequest request) {
        return ApiResponse.<RepairJobResponse>builder()
                .result(serviceManagementService.updateRepairJobStatus(jobId, request))
                .build();
    }

    @PostMapping("/repair-orders/{repairOrderId}/material-requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FOREMAN', 'MECHANIC')")
    @Operation(summary = "Create material request for a repair order", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<MaterialRequestResponse> createMaterialRequest(
            @PathVariable Long repairOrderId,
            @Valid @RequestBody CreateMaterialRequest request) {
        return ApiResponse.<MaterialRequestResponse>builder()
                .result(serviceManagementService.createMaterialRequest(repairOrderId, request))
                .build();
    }

    @PutMapping("/material-requests/{materialRequestId}/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOREKEEPER')")
    @Operation(summary = "Storekeeper issues parts and inventory is deducted automatically", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<MaterialRequestResponse> issueMaterial(
            @PathVariable Long materialRequestId,
            @Valid @RequestBody IssueServiceMaterialRequest request) {
        return ApiResponse.<MaterialRequestResponse>builder()
                .result(serviceManagementService.issueMaterial(materialRequestId, request))
                .build();
    }

    @PutMapping("/repair-orders/{repairOrderId}/qc/pass")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FOREMAN')")
    @Operation(summary = "Foreman confirms QC passed", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<RepairOrderResponse> passQc(@PathVariable Long repairOrderId) {
        return ApiResponse.<RepairOrderResponse>builder()
                .result(serviceManagementService.passQc(repairOrderId))
                .build();
    }

    @PutMapping("/repair-orders/{repairOrderId}/wash/done")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FOREMAN', 'WASHER')")
    @Operation(summary = "Confirm washing done", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<RepairOrderResponse> washDone(@PathVariable Long repairOrderId) {
        return ApiResponse.<RepairOrderResponse>builder()
                .result(serviceManagementService.washDone(repairOrderId))
                .build();
    }

    @PostMapping("/repair-orders/{repairOrderId}/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Create final invoice from actual repair order items", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<FinalInvoiceResponse> createFinalInvoice(
            @PathVariable Long repairOrderId,
            @Valid @RequestBody CreateFinalInvoiceRequest request) {
        return ApiResponse.<FinalInvoiceResponse>builder()
                .result(serviceManagementService.createFinalInvoice(repairOrderId, request))
                .build();
    }

    @PutMapping("/invoices/{invoiceId}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Cashier confirms payment", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<FinalInvoiceResponse> payInvoice(
            @PathVariable Long invoiceId,
            @Valid @RequestBody PayInvoiceRequest request) {
        return ApiResponse.<FinalInvoiceResponse>builder()
                .result(serviceManagementService.payInvoice(invoiceId, request))
                .build();
    }

    @PostMapping("/invoices/{invoiceId}/gate-pass")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Issue QR/barcode gate pass after payment", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<GatePassResponse> issueGatePass(@PathVariable Long invoiceId) {
        return ApiResponse.<GatePassResponse>builder()
                .result(serviceManagementService.issueGatePass(invoiceId))
                .build();
    }

    @PutMapping("/gate-passes/{code}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SECURITY')")
    @Operation(summary = "Security verifies gate pass before vehicle leaves garage", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<GatePassResponse> verifyGatePass(@PathVariable String code) {
        return ApiResponse.<GatePassResponse>builder()
                .result(serviceManagementService.verifyGatePass(code))
                .build();
    }
}
