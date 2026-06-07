package com.chennguyen.garagemanagement.DTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    String id;
    String fullName;
    String email;
    String phoneNumber;
    String address;
    String gender;
    LocalDate dob;
    String roleName;
    String roleDescription;
}
