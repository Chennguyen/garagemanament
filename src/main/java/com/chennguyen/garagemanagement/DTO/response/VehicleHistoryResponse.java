package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleHistoryResponse {
    VehicleResponse vehicle;
    List<ServiceVisitResponse> serviceVisits;
}
