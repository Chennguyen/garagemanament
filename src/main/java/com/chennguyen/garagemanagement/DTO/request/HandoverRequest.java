package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandoverRequest {

    @NotNull(message = "Asset ID không được bỏ trống")
    Long assetId;

    @NotBlank(message = "Staff ID không được bỏ trống")
    String staffId; // UUID của Staff

    String note;
}
