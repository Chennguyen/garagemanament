package com.chennguyen.garagemanagement.DTO.request;

import com.chennguyen.garagemanagement.emuns.RepairJobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRepairJobStatusRequest {
    @NotNull(message = "Job status is required")
    RepairJobStatus status;

    String mechanicNote;
}
