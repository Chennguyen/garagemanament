package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.RepairOrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairOrderResponse {
    Long id;
    Long serviceVisitId;
    Long quotationId;
    String licensePlate;
    RepairOrderStatus status;
    Boolean washRequested;
    String foremanId;
    String foremanName;
    LocalDateTime completedAt;
    LocalDateTime qcPassedAt;
    LocalDateTime washDoneAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<RepairJobResponse> jobs;
    List<MaterialRequestResponse> materialRequests;
}
