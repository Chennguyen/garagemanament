package com.chennguyen.garagemanagement.DTO.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KioskCheckInRequest {
    String username; // Mã nhân viên hoặc SĐT
    String password; // Mật khẩu (để xác thực)
    String action;   // "CHECK_IN" hoặc "CHECK_OUT"
}
