package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InspectionDamageResponse {
    Long id;
    String panel;
    String damageType;
    String severity;
    String description;
    String photoUrl;
}
