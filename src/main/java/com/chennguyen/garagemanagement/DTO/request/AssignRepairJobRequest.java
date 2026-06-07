package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignRepairJobRequest {
    @NotNull(message = "Repair job id is required")
    Long repairJobId;

    @NotBlank(message = "Mechanic id is required")
    String mechanicId;
}
