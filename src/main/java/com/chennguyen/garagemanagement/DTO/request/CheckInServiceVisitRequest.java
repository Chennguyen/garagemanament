package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.FuelLevel;
import com.chennguyen.garagemanagement.emuns.WorkflowType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckInServiceVisitRequest {
    @NotBlank(message = "License plate is required")
    String licensePlate;

    @NotNull(message = "Workflow type is required")
    WorkflowType workflowType;

    Boolean washRequested;
    Integer odometer;
    FuelLevel fuelLevel;
    String exteriorInspectionNote;
    String customerRequest;
    String advisorNote;
    List<InspectionDamageRequest> damages;
}
