package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.FuelLevel;
import com.chennguyen.garagemanagement.emuns.ServiceVisitStatus;
import com.chennguyen.garagemanagement.emuns.WorkflowType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceVisitResponse {
    Long id;
    VehicleResponse vehicle;
    WorkflowType workflowType;
    Boolean washRequested;
    Integer odometer;
    FuelLevel fuelLevel;
    String exteriorInspectionNote;
    String customerRequest;
    String advisorNote;
    String serviceAdvisorId;
    String serviceAdvisorName;
    ServiceVisitStatus status;
    LocalDateTime checkInAt;
    LocalDateTime updatedAt;
    List<InspectionDamageResponse> damages;
}
