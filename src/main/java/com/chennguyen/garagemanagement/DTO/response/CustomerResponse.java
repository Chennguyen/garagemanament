package com.chennguyen.garagemanagement.DTO.response;

import com.chennguyen.garagemanagement.emuns.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    Role role;
}
