package com.chennguyen.garagemanagement.DTO.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RejectQuotationRequest {
    String reason;
}
