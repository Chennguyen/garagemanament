package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IssueServiceMaterialRequest {
    @NotNull(message = "Issued quantities are required")
    Map<Long, Double> issuedQuantities;

    String note;
}
