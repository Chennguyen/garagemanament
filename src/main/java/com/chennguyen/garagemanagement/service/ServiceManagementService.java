package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.*;
import com.chennguyen.garagemanagement.DTO.response.*;
import com.chennguyen.garagemanagement.emuns.*;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceManagementService {
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ServiceCatalogItemRepository serviceCatalogItemRepository;
    private final ServiceVisitRepository serviceVisitRepository;
    private final InspectionDamageRepository inspectionDamageRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final RepairJobRepository repairJobRepository;
    private final MaterialRequestRepository materialRequestRepository;
    private final MaterialRequestItemRepository materialRequestItemRepository;
    private final FinalInvoiceRepository finalInvoiceRepository;
    private final FinalInvoiceItemRepository finalInvoiceItemRepository;
    private final GatePassRepository gatePassRepository;

    @Transactional
    public VehicleResponse upsertVehicle(UpsertVehicleRequest request) {
        Customer owner = customerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Vehicle vehicle = vehicleRepository.findById(normalizePlate(request.getLicensePlate()))
                .orElseGet(Vehicle::new);

        if (request.getVin() != null && !request.getVin().isBlank()) {
            vehicleRepository.findByVin(request.getVin())
                    .filter(existing -> !Objects.equals(existing.getLicensePlate(), vehicle.getLicensePlate()))
                    .ifPresent(existing -> {
                        throw new AppException(ErrorCode.VEHICLE_VIN_EXISTED);
                    });
        }

        vehicle.setLicensePlate(normalizePlate(request.getLicensePlate()));
        vehicle.setVin(blankToNull(request.getVin()));
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setProductionYear(request.getProductionYear());
        vehicle.setColor(request.getColor());
        vehicle.setOwner(owner);
        vehicle.setOwnerTaxCode(request.getOwnerTaxCode());

        return toVehicleResponse(vehicleRepository.save(vehicle));
    }

    public VehicleResponse getVehicle(String licensePlate) {
        return toVehicleResponse(getVehicleEntity(licensePlate));
    }

    public VehicleHistoryResponse getVehicleHistory(String licensePlate) {
        Vehicle vehicle = getVehicleEntity(licensePlate);
        List<ServiceVisitResponse> visits = serviceVisitRepository.findByVehicleOrderByCheckInAtDesc(vehicle)
                .stream()
                .map(this::toServiceVisitResponse)
                .toList();

        return VehicleHistoryResponse.builder()
                .vehicle(toVehicleResponse(vehicle))
                .serviceVisits(visits)
                .build();
    }

    @Transactional
    public ServiceCatalogItemResponse createServiceCatalogItem(CreateServiceCatalogItemRequest request) {
        if (serviceCatalogItemRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.SERVICE_CODE_EXISTED);
        }

        ServiceCatalogItem item = ServiceCatalogItem.builder()
                .code(request.getCode())
                .name(request.getName())
                .category(request.getCategory())
                .standardPrice(nullToZero(request.getStandardPrice()))
                .estimatedMinutes(request.getEstimatedMinutes())
                .active(request.getActive() == null || request.getActive())
                .build();

        return toServiceCatalogItemResponse(serviceCatalogItemRepository.save(item));
    }

    public List<ServiceCatalogItemResponse> getActiveServiceCatalogItems() {
        return serviceCatalogItemRepository.findByActiveTrue()
                .stream()
                .map(this::toServiceCatalogItemResponse)
                .toList();
    }

    @Transactional
    public ServiceVisitResponse checkIn(CheckInServiceVisitRequest request) {
        Vehicle vehicle = getVehicleEntity(request.getLicensePlate());
        Staff advisor = getCurrentAuthenticatedStaff();
        boolean washRequested = request.getWorkflowType() == WorkflowType.QUICK_WASH_DETAILING
                || request.getWashRequested() == null
                || request.getWashRequested();

        ServiceVisit visit = ServiceVisit.builder()
                .vehicle(vehicle)
                .workflowType(request.getWorkflowType())
                .washRequested(washRequested)
                .odometer(request.getOdometer())
                .fuelLevel(request.getFuelLevel())
                .exteriorInspectionNote(request.getExteriorInspectionNote())
                .customerRequest(request.getCustomerRequest())
                .advisorNote(request.getAdvisorNote())
                .serviceAdvisor(advisor)
                .status(request.getWorkflowType() == WorkflowType.QUICK_WASH_DETAILING
                        ? ServiceVisitStatus.WASHING
                        : ServiceVisitStatus.CHECKED_IN)
                .build();

        visit = serviceVisitRepository.save(visit);
        saveDamages(visit, request.getDamages());

        if (request.getWorkflowType() == WorkflowType.QUICK_WASH_DETAILING) {
            repairOrderRepository.save(RepairOrder.builder()
                    .serviceVisit(visit)
                    .status(RepairOrderStatus.WASHING)
                    .washRequested(true)
                    .foreman(advisor)
                    .build());
        }

        return toServiceVisitResponse(visit);
    }

    public ServiceVisitResponse getServiceVisit(Long visitId) {
        return toServiceVisitResponse(getServiceVisitEntity(visitId));
    }

    @Transactional
    public QuotationResponse createQuotation(CreateQuotationRequest request) {
        ServiceVisit visit = getServiceVisitEntity(request.getServiceVisitId());
        if (visit.getWorkflowType() == WorkflowType.QUICK_WASH_DETAILING) {
            throw new AppException(ErrorCode.INVALID_SERVICE_WORKFLOW);
        }

        Quotation quotation = Quotation.builder()
                .serviceVisit(visit)
                .status(QuotationStatus.DRAFT)
                .subtotalParts(BigDecimal.ZERO)
                .subtotalLabor(BigDecimal.ZERO)
                .otherFees(BigDecimal.ZERO)
                .discountAmount(nullToZero(request.getDiscountAmount()))
                .totalAmount(BigDecimal.ZERO)
                .note(request.getNote())
                .build();
        quotation = quotationRepository.save(quotation);

        BigDecimal parts = BigDecimal.ZERO;
        BigDecimal labor = BigDecimal.ZERO;
        BigDecimal other = BigDecimal.ZERO;
        for (QuotationItemRequest itemRequest : request.getItems()) {
            QuotationItem item = buildQuotationItem(quotation, itemRequest);
            quotationItemRepository.save(item);

            if (item.getType() == QuotationItemType.PART) {
                parts = parts.add(item.getTotalPrice());
            } else if (item.getType() == QuotationItemType.LABOR) {
                labor = labor.add(item.getTotalPrice());
            } else {
                other = other.add(item.getTotalPrice());
            }
        }

        quotation.setSubtotalParts(parts);
        quotation.setSubtotalLabor(labor);
        quotation.setOtherFees(other);
        quotation.setTotalAmount(parts.add(labor).add(other).subtract(quotation.getDiscountAmount()));
        quotation = quotationRepository.save(quotation);

        visit.setStatus(ServiceVisitStatus.QUOTING);
        serviceVisitRepository.save(visit);

        return toQuotationResponse(quotation);
    }

    @Transactional
    public QuotationResponse sendQuotation(Long quotationId) {
        Quotation quotation = getQuotationEntity(quotationId);
        quotation.setStatus(QuotationStatus.SENT);
        quotation.setSentAt(LocalDateTime.now());
        quotation.getServiceVisit().setStatus(ServiceVisitStatus.QUOTATION_SENT);
        return toQuotationResponse(quotationRepository.save(quotation));
    }

    @Transactional
    public RepairOrderResponse approveQuotation(Long quotationId) {
        Quotation quotation = getQuotationEntity(quotationId);
        if (quotation.getStatus() == QuotationStatus.REJECTED) {
            throw new AppException(ErrorCode.INVALID_QUOTATION_STATUS);
        }

        quotation.setStatus(QuotationStatus.APPROVED);
        quotation.setApprovedAt(LocalDateTime.now());
        quotationRepository.save(quotation);

        RepairOrder repairOrder = repairOrderRepository.findByQuotation(quotation)
                .orElseGet(() -> createRepairOrderFromQuotation(quotation, getCurrentAuthenticatedStaff()));

        return toRepairOrderResponse(repairOrder);
    }

    @Transactional
    public QuotationResponse rejectQuotation(Long quotationId, RejectQuotationRequest request) {
        Quotation quotation = getQuotationEntity(quotationId);
        quotation.setStatus(QuotationStatus.REJECTED);
        quotation.setRejectedAt(LocalDateTime.now());
        if (request != null && request.getReason() != null) {
            quotation.setNote(appendNote(quotation.getNote(), "Rejected: " + request.getReason()));
        }
        quotation.getServiceVisit().setStatus(ServiceVisitStatus.CANCELLED);
        return toQuotationResponse(quotationRepository.save(quotation));
    }

    public RepairOrderResponse getRepairOrder(Long repairOrderId) {
        return toRepairOrderResponse(getRepairOrderEntity(repairOrderId));
    }

    @Transactional
    public RepairJobResponse assignRepairJob(AssignRepairJobRequest request) {
        RepairJob job = repairJobRepository.findById(request.getRepairJobId())
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_JOB_NOT_FOUND));
        Staff mechanic = staffRepository.findById(request.getMechanicId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        job.setAssignedMechanic(mechanic);
        job.setAssignedAt(LocalDateTime.now());
        job.setStatus(RepairJobStatus.ASSIGNED);

        RepairOrder repairOrder = job.getRepairOrder();
        if (repairOrder.getStatus() == RepairOrderStatus.READY_TO_WORK) {
            repairOrder.setStatus(RepairOrderStatus.IN_PROGRESS);
            repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.REPAIRING);
        }

        return toRepairJobResponse(repairJobRepository.save(job));
    }

    @Transactional
    public RepairJobResponse updateRepairJobStatus(Long jobId, UpdateRepairJobStatusRequest request) {
        RepairJob job = repairJobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_JOB_NOT_FOUND));

        job.setStatus(request.getStatus());
        if (request.getMechanicNote() != null) {
            job.setMechanicNote(request.getMechanicNote());
        }
        if (request.getStatus() == RepairJobStatus.IN_PROGRESS && job.getStartedAt() == null) {
            job.setStartedAt(LocalDateTime.now());
            job.getRepairOrder().setStatus(RepairOrderStatus.IN_PROGRESS);
            job.getRepairOrder().getServiceVisit().setStatus(ServiceVisitStatus.REPAIRING);
        }
        if (request.getStatus() == RepairJobStatus.COMPLETED) {
            job.setCompletedAt(LocalDateTime.now());
        }

        RepairJob saved = repairJobRepository.save(job);
        markRepairOrderCompletedIfReady(job.getRepairOrder());
        return toRepairJobResponse(saved);
    }

    @Transactional
    public MaterialRequestResponse createMaterialRequest(Long repairOrderId, CreateMaterialRequest request) {
        RepairOrder repairOrder = getRepairOrderEntity(repairOrderId);
        Staff requester = getCurrentAuthenticatedStaff();

        MaterialRequest materialRequest = materialRequestRepository.save(MaterialRequest.builder()
                .repairOrder(repairOrder)
                .status(MaterialRequestStatus.REQUESTED)
                .requestedBy(requester)
                .note(request.getNote())
                .build());

        for (MaterialRequestItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            materialRequestItemRepository.save(MaterialRequestItem.builder()
                    .materialRequest(materialRequest)
                    .product(product)
                    .requestedQuantity(itemRequest.getQuantity())
                    .issuedQuantity(0.0)
                    .build());
        }

        repairOrder.setStatus(RepairOrderStatus.WAITING_PARTS);
        repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.WAITING_PARTS);
        repairOrderRepository.save(repairOrder);

        return toMaterialRequestResponse(materialRequest);
    }

    @Transactional
    public MaterialRequestResponse issueMaterial(Long materialRequestId, IssueServiceMaterialRequest request) {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.MATERIAL_REQUEST_NOT_FOUND));
        Staff issuer = getCurrentAuthenticatedStaff();
        Map<Long, Double> issueMap = request.getIssuedQuantities();

        for (MaterialRequestItem item : materialRequestItemRepository.findByMaterialRequest(materialRequest)) {
            Double issueQty = issueMap.getOrDefault(item.getId(), 0.0);
            if (issueQty == null || issueQty <= 0) {
                continue;
            }
            double alreadyIssued = item.getIssuedQuantity() == null ? 0.0 : item.getIssuedQuantity();
            if (alreadyIssued + issueQty > item.getRequestedQuantity()) {
                throw new AppException(ErrorCode.INVALID_MATERIAL_QUANTITY);
            }

            Product product = item.getProduct();
            double currentStock = product.getCurrentStock() == null ? 0.0 : product.getCurrentStock();
            if (currentStock < issueQty) {
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
            }

            double newStock = currentStock - issueQty;
            product.setCurrentStock(newStock);
            productRepository.save(product);

            item.setIssuedQuantity(alreadyIssued + issueQty);
            materialRequestItemRepository.save(item);

            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .product(product)
                    .type(TransactionType.OUTBOUND_SERVICE)
                    .quantity(-issueQty)
                    .stockAfterTransaction(newStock)
                    .referenceId("RO-" + materialRequest.getRepairOrder().getId())
                    .note(request.getNote())
                    .performedBy(issuer)
                    .build());
        }

        materialRequest.setIssuedBy(issuer);
        materialRequest.setIssuedAt(LocalDateTime.now());
        materialRequest.setStatus(resolveMaterialRequestStatus(materialRequest));
        MaterialRequest saved = materialRequestRepository.save(materialRequest);

        if (saved.getStatus() == MaterialRequestStatus.ISSUED) {
            RepairOrder repairOrder = saved.getRepairOrder();
            repairOrder.setStatus(RepairOrderStatus.READY_TO_WORK);
            repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.APPROVED);
            repairOrderRepository.save(repairOrder);
        }

        return toMaterialRequestResponse(saved);
    }

    @Transactional
    public RepairOrderResponse passQc(Long repairOrderId) {
        RepairOrder repairOrder = getRepairOrderEntity(repairOrderId);
        repairOrder.setQcPassedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(repairOrder.getWashRequested())) {
            repairOrder.setStatus(RepairOrderStatus.WASHING);
            repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.WASHING);
        } else {
            repairOrder.setStatus(RepairOrderStatus.BILLING);
            repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.BILLING);
        }

        return toRepairOrderResponse(repairOrderRepository.save(repairOrder));
    }

    @Transactional
    public RepairOrderResponse washDone(Long repairOrderId) {
        RepairOrder repairOrder = getRepairOrderEntity(repairOrderId);
        repairOrder.setWashDoneAt(LocalDateTime.now());
        repairOrder.setStatus(RepairOrderStatus.WASH_DONE);
        repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.BILLING);
        return toRepairOrderResponse(repairOrderRepository.save(repairOrder));
    }

    @Transactional
    public FinalInvoiceResponse createFinalInvoice(Long repairOrderId, CreateFinalInvoiceRequest request) {
        RepairOrder repairOrder = getRepairOrderEntity(repairOrderId);
        finalInvoiceRepository.findByRepairOrder(repairOrder).ifPresent(invoice -> {
            throw new AppException(ErrorCode.INVOICE_ALREADY_CREATED);
        });

        Staff cashier = getCurrentAuthenticatedStaff();
        FinalInvoice invoice = finalInvoiceRepository.save(FinalInvoice.builder()
                .repairOrder(repairOrder)
                .status(InvoiceStatus.ISSUED)
                .subtotalAmount(BigDecimal.ZERO)
                .discountAmount(nullToZero(request.getDiscountAmount()))
                .discountCode(request.getDiscountCode())
                .totalAmount(BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .cashier(cashier)
                .note(request.getNote())
                .issuedAt(LocalDateTime.now())
                .build());

        BigDecimal subtotal = copyQuotationItemsToInvoice(invoice, repairOrder);
        subtotal = subtotal.add(saveAdditionalInvoiceItems(invoice, request.getAdditionalItems()));
        invoice.setSubtotalAmount(subtotal);
        invoice.setTotalAmount(subtotal.subtract(invoice.getDiscountAmount()));
        invoice = finalInvoiceRepository.save(invoice);

        repairOrder.setStatus(RepairOrderStatus.BILLING);
        repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.BILLING);
        repairOrderRepository.save(repairOrder);

        return toFinalInvoiceResponse(invoice);
    }

    @Transactional
    public FinalInvoiceResponse payInvoice(Long invoiceId, PayInvoiceRequest request) {
        FinalInvoice invoice = finalInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        if (request.getPaymentMethod() != PaymentMethod.DEBT
                && request.getPaidAmount().compareTo(invoice.getTotalAmount()) < 0) {
            throw new AppException(ErrorCode.PAYMENT_NOT_ENOUGH);
        }

        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setPaidAmount(request.getPaidAmount());
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setNote(appendNote(invoice.getNote(), request.getNote()));
        invoice.getRepairOrder().getServiceVisit().setStatus(ServiceVisitStatus.PAID);

        return toFinalInvoiceResponse(finalInvoiceRepository.save(invoice));
    }

    @Transactional
    public GatePassResponse issueGatePass(Long invoiceId) {
        FinalInvoice invoice = finalInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        if (invoice.getStatus() != InvoiceStatus.PAID) {
            throw new AppException(ErrorCode.INVOICE_NOT_PAID);
        }

        GatePass pass = gatePassRepository.findByInvoice(invoice).orElse(null);
        if (pass == null) {
            String code = "GP-" + invoice.getId() + "-" + System.currentTimeMillis();
            pass = GatePass.builder()
                    .invoice(invoice)
                    .code(code)
                    .qrContent("GATE_PASS:" + code + ":INVOICE:" + invoice.getId())
                    .status(GatePassStatus.ACTIVE)
                    .issuedBy(getCurrentAuthenticatedStaff())
                    .build();
        }

        invoice.getRepairOrder().setStatus(RepairOrderStatus.CLOSED);
        invoice.getRepairOrder().getServiceVisit().setStatus(ServiceVisitStatus.GATE_PASS_ISSUED);
        return toGatePassResponse(gatePassRepository.save(pass));
    }

    @Transactional
    public GatePassResponse verifyGatePass(String code) {
        GatePass pass = gatePassRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.GATE_PASS_NOT_FOUND));
        if (pass.getStatus() != GatePassStatus.ACTIVE) {
            throw new AppException(ErrorCode.INVALID_GATE_PASS_STATUS);
        }
        pass.setStatus(GatePassStatus.USED);
        pass.setVerifiedAt(LocalDateTime.now());
        pass.setVerifiedBy(getCurrentAuthenticatedStaff());
        pass.getInvoice().getRepairOrder().getServiceVisit().setStatus(ServiceVisitStatus.DELIVERED);

        return toGatePassResponse(gatePassRepository.save(pass));
    }

    private RepairOrder createRepairOrderFromQuotation(Quotation quotation, Staff foreman) {
        boolean hasParts = quotationItemRepository.findByQuotation(quotation).stream()
                .anyMatch(item -> item.getType() == QuotationItemType.PART);
        RepairOrder repairOrder = repairOrderRepository.save(RepairOrder.builder()
                .serviceVisit(quotation.getServiceVisit())
                .quotation(quotation)
                .status(hasParts ? RepairOrderStatus.WAITING_PARTS : RepairOrderStatus.READY_TO_WORK)
                .washRequested(quotation.getServiceVisit().getWashRequested())
                .foreman(foreman)
                .build());

        List<QuotationItem> items = quotationItemRepository.findByQuotation(quotation);
        items.stream()
                .filter(item -> item.getType() == QuotationItemType.LABOR)
                .forEach(item -> repairJobRepository.save(RepairJob.builder()
                        .repairOrder(repairOrder)
                        .serviceCatalogItem(item.getServiceCatalogItem())
                        .description(item.getDescription())
                        .status(RepairJobStatus.WAITING_ASSIGNMENT)
                        .build()));

        if (hasParts) {
            MaterialRequest materialRequest = materialRequestRepository.save(MaterialRequest.builder()
                    .repairOrder(repairOrder)
                    .status(MaterialRequestStatus.REQUESTED)
                    .requestedBy(foreman)
                    .note("Auto created from approved quotation")
                    .build());
            items.stream()
                    .filter(item -> item.getType() == QuotationItemType.PART)
                    .forEach(item -> materialRequestItemRepository.save(MaterialRequestItem.builder()
                            .materialRequest(materialRequest)
                            .product(item.getProduct())
                            .requestedQuantity(item.getQuantity())
                            .issuedQuantity(0.0)
                            .build()));
        }

        quotation.getServiceVisit().setStatus(hasParts ? ServiceVisitStatus.WAITING_PARTS : ServiceVisitStatus.APPROVED);
        serviceVisitRepository.save(quotation.getServiceVisit());
        return repairOrder;
    }

    private QuotationItem buildQuotationItem(Quotation quotation, QuotationItemRequest request) {
        Product product = null;
        ServiceCatalogItem serviceCatalogItem = null;
        BigDecimal unitPrice = request.getUnitPrice();
        String description = request.getDescription();

        if (request.getType() == QuotationItemType.PART) {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if ((product.getCurrentStock() == null ? 0.0 : product.getCurrentStock()) < request.getQuantity()) {
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
            }
            if (unitPrice == null) {
                unitPrice = product.getSellingPrice();
            }
        } else if (request.getType() == QuotationItemType.LABOR) {
            serviceCatalogItem = serviceCatalogItemRepository.findById(request.getServiceCatalogItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATALOG_ITEM_NOT_FOUND));
            if (unitPrice == null) {
                unitPrice = serviceCatalogItem.getStandardPrice();
            }
        } else if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO;
        }

        return QuotationItem.builder()
                .quotation(quotation)
                .type(request.getType())
                .product(product)
                .serviceCatalogItem(serviceCatalogItem)
                .description(description)
                .quantity(request.getQuantity())
                .unitPrice(nullToZero(unitPrice))
                .totalPrice(nullToZero(unitPrice).multiply(BigDecimal.valueOf(request.getQuantity())))
                .build();
    }

    private void saveDamages(ServiceVisit visit, List<InspectionDamageRequest> damages) {
        if (damages == null) {
            return;
        }
        for (InspectionDamageRequest damage : damages) {
            inspectionDamageRepository.save(InspectionDamage.builder()
                    .serviceVisit(visit)
                    .panel(damage.getPanel())
                    .damageType(damage.getDamageType())
                    .severity(damage.getSeverity())
                    .description(damage.getDescription())
                    .photoUrl(damage.getPhotoUrl())
                    .build());
        }
    }

    private void markRepairOrderCompletedIfReady(RepairOrder repairOrder) {
        List<RepairJob> jobs = repairJobRepository.findByRepairOrder(repairOrder);
        if (!jobs.isEmpty() && jobs.stream().allMatch(job -> job.getStatus() == RepairJobStatus.COMPLETED)) {
            repairOrder.setStatus(RepairOrderStatus.COMPLETED);
            repairOrder.setCompletedAt(LocalDateTime.now());
            repairOrder.getServiceVisit().setStatus(ServiceVisitStatus.QC);
            repairOrderRepository.save(repairOrder);
        }
    }

    private MaterialRequestStatus resolveMaterialRequestStatus(MaterialRequest materialRequest) {
        List<MaterialRequestItem> items = materialRequestItemRepository.findByMaterialRequest(materialRequest);
        boolean allIssued = items.stream()
                .allMatch(item -> item.getIssuedQuantity() != null
                        && item.getIssuedQuantity() >= item.getRequestedQuantity());
        boolean anyIssued = items.stream()
                .anyMatch(item -> item.getIssuedQuantity() != null && item.getIssuedQuantity() > 0);
        if (allIssued) {
            return MaterialRequestStatus.ISSUED;
        }
        return anyIssued ? MaterialRequestStatus.PARTIALLY_ISSUED : MaterialRequestStatus.REQUESTED;
    }

    private BigDecimal copyQuotationItemsToInvoice(FinalInvoice invoice, RepairOrder repairOrder) {
        if (repairOrder.getQuotation() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (QuotationItem item : quotationItemRepository.findByQuotation(repairOrder.getQuotation())) {
            finalInvoiceItemRepository.save(FinalInvoiceItem.builder()
                    .invoice(invoice)
                    .type(item.getType())
                    .product(item.getProduct())
                    .serviceCatalogItem(item.getServiceCatalogItem())
                    .description(item.getDescription())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .totalPrice(item.getTotalPrice())
                    .build());
            subtotal = subtotal.add(item.getTotalPrice());
        }
        return subtotal;
    }

    private BigDecimal saveAdditionalInvoiceItems(FinalInvoice invoice, List<FinalInvoiceItemRequest> additionalItems) {
        if (additionalItems == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (FinalInvoiceItemRequest request : additionalItems) {
            Product product = null;
            ServiceCatalogItem serviceCatalogItem = null;
            if (request.getProductId() != null) {
                product = productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            }
            if (request.getServiceCatalogItemId() != null) {
                serviceCatalogItem = serviceCatalogItemRepository.findById(request.getServiceCatalogItemId())
                        .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATALOG_ITEM_NOT_FOUND));
            }

            BigDecimal total = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            subtotal = subtotal.add(total);
            finalInvoiceItemRepository.save(FinalInvoiceItem.builder()
                    .invoice(invoice)
                    .type(request.getType())
                    .product(product)
                    .serviceCatalogItem(serviceCatalogItem)
                    .description(request.getDescription())
                    .quantity(request.getQuantity())
                    .unitPrice(request.getUnitPrice())
                    .totalPrice(total)
                    .build());
        }
        return subtotal;
    }

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

    private Vehicle getVehicleEntity(String licensePlate) {
        return vehicleRepository.findById(normalizePlate(licensePlate))
                .orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));
    }

    private ServiceVisit getServiceVisitEntity(Long visitId) {
        return serviceVisitRepository.findById(visitId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_VISIT_NOT_FOUND));
    }

    private Quotation getQuotationEntity(Long quotationId) {
        return quotationRepository.findById(quotationId)
                .orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));
    }

    private RepairOrder getRepairOrderEntity(Long repairOrderId) {
        return repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_ORDER_NOT_FOUND));
    }

    private VehicleResponse toVehicleResponse(Vehicle vehicle) {
        Customer owner = vehicle.getOwner();
        return VehicleResponse.builder()
                .licensePlate(vehicle.getLicensePlate())
                .vin(vehicle.getVin())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .productionYear(vehicle.getProductionYear())
                .color(vehicle.getColor())
                .ownerId(owner != null ? owner.getId() : null)
                .ownerName(owner != null ? owner.getFullName() : null)
                .ownerPhoneNumber(owner != null ? owner.getPhoneNumber() : null)
                .ownerAddress(owner != null ? owner.getAddress() : null)
                .ownerTaxCode(vehicle.getOwnerTaxCode())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    private ServiceCatalogItemResponse toServiceCatalogItemResponse(ServiceCatalogItem item) {
        return ServiceCatalogItemResponse.builder()
                .id(item.getId())
                .code(item.getCode())
                .name(item.getName())
                .category(item.getCategory())
                .standardPrice(item.getStandardPrice())
                .estimatedMinutes(item.getEstimatedMinutes())
                .active(item.getActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private ServiceVisitResponse toServiceVisitResponse(ServiceVisit visit) {
        return ServiceVisitResponse.builder()
                .id(visit.getId())
                .vehicle(toVehicleResponse(visit.getVehicle()))
                .workflowType(visit.getWorkflowType())
                .washRequested(visit.getWashRequested())
                .odometer(visit.getOdometer())
                .fuelLevel(visit.getFuelLevel())
                .exteriorInspectionNote(visit.getExteriorInspectionNote())
                .customerRequest(visit.getCustomerRequest())
                .advisorNote(visit.getAdvisorNote())
                .serviceAdvisorId(visit.getServiceAdvisor() != null ? visit.getServiceAdvisor().getId() : null)
                .serviceAdvisorName(visit.getServiceAdvisor() != null ? visit.getServiceAdvisor().getFullName() : null)
                .status(visit.getStatus())
                .checkInAt(visit.getCheckInAt())
                .updatedAt(visit.getUpdatedAt())
                .damages(inspectionDamageRepository.findByServiceVisit(visit).stream()
                        .map(this::toInspectionDamageResponse)
                        .toList())
                .build();
    }

    private InspectionDamageResponse toInspectionDamageResponse(InspectionDamage damage) {
        return InspectionDamageResponse.builder()
                .id(damage.getId())
                .panel(damage.getPanel())
                .damageType(damage.getDamageType())
                .severity(damage.getSeverity())
                .description(damage.getDescription())
                .photoUrl(damage.getPhotoUrl())
                .build();
    }

    private QuotationResponse toQuotationResponse(Quotation quotation) {
        return QuotationResponse.builder()
                .id(quotation.getId())
                .serviceVisitId(quotation.getServiceVisit().getId())
                .licensePlate(quotation.getServiceVisit().getVehicle().getLicensePlate())
                .status(quotation.getStatus())
                .subtotalParts(quotation.getSubtotalParts())
                .subtotalLabor(quotation.getSubtotalLabor())
                .otherFees(quotation.getOtherFees())
                .discountAmount(quotation.getDiscountAmount())
                .totalAmount(quotation.getTotalAmount())
                .note(quotation.getNote())
                .sentAt(quotation.getSentAt())
                .approvedAt(quotation.getApprovedAt())
                .rejectedAt(quotation.getRejectedAt())
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .items(quotationItemRepository.findByQuotation(quotation).stream()
                        .map(this::toQuotationItemResponse)
                        .toList())
                .build();
    }

    private QuotationItemResponse toQuotationItemResponse(QuotationItem item) {
        Product product = item.getProduct();
        ServiceCatalogItem catalogItem = item.getServiceCatalogItem();
        return QuotationItemResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .sku(product != null ? product.getSku() : null)
                .currentStock(product != null ? product.getCurrentStock() : null)
                .serviceCatalogItemId(catalogItem != null ? catalogItem.getId() : null)
                .serviceCatalogCode(catalogItem != null ? catalogItem.getCode() : null)
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private RepairOrderResponse toRepairOrderResponse(RepairOrder repairOrder) {
        return RepairOrderResponse.builder()
                .id(repairOrder.getId())
                .serviceVisitId(repairOrder.getServiceVisit().getId())
                .quotationId(repairOrder.getQuotation() != null ? repairOrder.getQuotation().getId() : null)
                .licensePlate(repairOrder.getServiceVisit().getVehicle().getLicensePlate())
                .status(repairOrder.getStatus())
                .washRequested(repairOrder.getWashRequested())
                .foremanId(repairOrder.getForeman() != null ? repairOrder.getForeman().getId() : null)
                .foremanName(repairOrder.getForeman() != null ? repairOrder.getForeman().getFullName() : null)
                .completedAt(repairOrder.getCompletedAt())
                .qcPassedAt(repairOrder.getQcPassedAt())
                .washDoneAt(repairOrder.getWashDoneAt())
                .createdAt(repairOrder.getCreatedAt())
                .updatedAt(repairOrder.getUpdatedAt())
                .jobs(repairJobRepository.findByRepairOrder(repairOrder).stream()
                        .map(this::toRepairJobResponse)
                        .toList())
                .materialRequests(materialRequestRepository.findByRepairOrderOrderByCreatedAtDesc(repairOrder).stream()
                        .map(this::toMaterialRequestResponse)
                        .toList())
                .build();
    }

    private RepairJobResponse toRepairJobResponse(RepairJob job) {
        ServiceCatalogItem catalogItem = job.getServiceCatalogItem();
        Staff mechanic = job.getAssignedMechanic();
        return RepairJobResponse.builder()
                .id(job.getId())
                .repairOrderId(job.getRepairOrder().getId())
                .serviceCatalogItemId(catalogItem != null ? catalogItem.getId() : null)
                .serviceCatalogCode(catalogItem != null ? catalogItem.getCode() : null)
                .description(job.getDescription())
                .status(job.getStatus())
                .assignedMechanicId(mechanic != null ? mechanic.getId() : null)
                .assignedMechanicName(mechanic != null ? mechanic.getFullName() : null)
                .assignedAt(job.getAssignedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .mechanicNote(job.getMechanicNote())
                .build();
    }

    private MaterialRequestResponse toMaterialRequestResponse(MaterialRequest materialRequest) {
        return MaterialRequestResponse.builder()
                .id(materialRequest.getId())
                .repairOrderId(materialRequest.getRepairOrder().getId())
                .status(materialRequest.getStatus())
                .requestedByName(materialRequest.getRequestedBy() != null ? materialRequest.getRequestedBy().getFullName() : null)
                .issuedByName(materialRequest.getIssuedBy() != null ? materialRequest.getIssuedBy().getFullName() : null)
                .note(materialRequest.getNote())
                .issuedAt(materialRequest.getIssuedAt())
                .createdAt(materialRequest.getCreatedAt())
                .updatedAt(materialRequest.getUpdatedAt())
                .items(materialRequestItemRepository.findByMaterialRequest(materialRequest).stream()
                        .map(this::toMaterialRequestItemResponse)
                        .toList())
                .build();
    }

    private MaterialRequestItemResponse toMaterialRequestItemResponse(MaterialRequestItem item) {
        Product product = item.getProduct();
        return MaterialRequestItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .requestedQuantity(item.getRequestedQuantity())
                .issuedQuantity(item.getIssuedQuantity())
                .currentStock(product.getCurrentStock())
                .build();
    }

    private FinalInvoiceResponse toFinalInvoiceResponse(FinalInvoice invoice) {
        return FinalInvoiceResponse.builder()
                .id(invoice.getId())
                .repairOrderId(invoice.getRepairOrder().getId())
                .licensePlate(invoice.getRepairOrder().getServiceVisit().getVehicle().getLicensePlate())
                .status(invoice.getStatus())
                .subtotalAmount(invoice.getSubtotalAmount())
                .discountAmount(invoice.getDiscountAmount())
                .discountCode(invoice.getDiscountCode())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .paymentMethod(invoice.getPaymentMethod())
                .cashierName(invoice.getCashier() != null ? invoice.getCashier().getFullName() : null)
                .note(invoice.getNote())
                .issuedAt(invoice.getIssuedAt())
                .paidAt(invoice.getPaidAt())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .items(finalInvoiceItemRepository.findByInvoice(invoice).stream()
                        .map(this::toFinalInvoiceItemResponse)
                        .toList())
                .build();
    }

    private FinalInvoiceItemResponse toFinalInvoiceItemResponse(FinalInvoiceItem item) {
        Product product = item.getProduct();
        ServiceCatalogItem catalogItem = item.getServiceCatalogItem();
        return FinalInvoiceItemResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .serviceCatalogItemId(catalogItem != null ? catalogItem.getId() : null)
                .serviceCatalogCode(catalogItem != null ? catalogItem.getCode() : null)
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private GatePassResponse toGatePassResponse(GatePass pass) {
        RepairOrder repairOrder = pass.getInvoice().getRepairOrder();
        return GatePassResponse.builder()
                .id(pass.getId())
                .invoiceId(pass.getInvoice().getId())
                .repairOrderId(repairOrder.getId())
                .licensePlate(repairOrder.getServiceVisit().getVehicle().getLicensePlate())
                .code(pass.getCode())
                .qrContent(pass.getQrContent())
                .status(pass.getStatus())
                .issuedByName(pass.getIssuedBy() != null ? pass.getIssuedBy().getFullName() : null)
                .verifiedByName(pass.getVerifiedBy() != null ? pass.getVerifiedBy().getFullName() : null)
                .issuedAt(pass.getIssuedAt())
                .verifiedAt(pass.getVerifiedAt())
                .build();
    }

    private String normalizePlate(String licensePlate) {
        return licensePlate == null ? null : licensePlate.trim().toUpperCase();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String appendNote(String current, String addition) {
        if (addition == null || addition.isBlank()) {
            return current;
        }
        if (current == null || current.isBlank()) {
            return addition;
        }
        return current + "\n" + addition;
    }
}
