package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, message = "Họ tên không được để trống")
    String fullName;
    
    @Email(message = "Email không hợp lệ")
    String email;
    
    @Size(min = 1, message = "Địa chỉ không được để trống")
    String address;
    
    String gender;
    LocalDate dob;
}
