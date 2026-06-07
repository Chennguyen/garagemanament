package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.RepairJobStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairJobResponse {
    Long id;
    Long repairOrderId;
    Long serviceCatalogItemId;
    String serviceCatalogCode;
    String description;
    RepairJobStatus status;
    String assignedMechanicId;
    String assignedMechanicName;
    LocalDateTime assignedAt;
    LocalDateTime startedAt;
    LocalDateTime completedAt;
    String mechanicNote;
}
