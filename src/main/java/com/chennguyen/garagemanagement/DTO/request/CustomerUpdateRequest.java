package com.chennguyen.garagemanagement.DTO.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateRequest {
    String fullName;
    String address;
    String gender;
    LocalDate dob;
}
