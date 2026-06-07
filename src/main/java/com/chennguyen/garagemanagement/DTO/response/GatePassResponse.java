package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.GatePassStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GatePassResponse {
    Long id;
    Long invoiceId;
    Long repairOrderId;
    String licensePlate;
    String code;
    String qrContent;
    GatePassStatus status;
    String issuedByName;
    String verifiedByName;
    LocalDateTime issuedAt;
    LocalDateTime verifiedAt;
}
