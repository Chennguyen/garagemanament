package com.chennguyen.garagemanagement.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistrationRequest {
    
    @NotBlank(message = "Họ và tên không được bỏ trống")
    String fullName;
    
    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không hợp lệ")
    String email;
    
    @NotBlank(message = "Địa chỉ không được bỏ trống")
    String address;
    
    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "^(03|05|07|08|09)\\d{8}$", message = "Số điện thoại không hợp lệ")
    String phoneNumber;
    
    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Mật khẩu tối thiểu 8 ký tự, có cả chữ và số")
    String password;
    
    @NotBlank(message = "Xác nhận mật khẩu không được bỏ trống")
    String confirmPassword;
}
