package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.MaterialRequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialRequestResponse {
    Long id;
    Long repairOrderId;
    MaterialRequestStatus status;
    String requestedByName;
    String issuedByName;
    String note;
    LocalDateTime issuedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<MaterialRequestItemResponse> items;
}
