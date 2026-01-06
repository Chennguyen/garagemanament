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
public class StaffUpdateRequest {
    String fullName;
    String address; // (Thay cho hometown để khớp Entity)
    LocalDate dob;
    String gender;
    String avatar;
    String bio;
}
