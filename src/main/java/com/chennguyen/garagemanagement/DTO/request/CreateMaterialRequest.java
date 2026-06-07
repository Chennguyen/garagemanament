package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMaterialRequest {
    String note;

    @Valid
    @NotEmpty(message = "Material request items are required")
    List<MaterialRequestItemRequest> items;
}
