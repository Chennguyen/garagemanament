package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.AuthenticationRequest;
import com.chennguyen.garagemanagement.DTO.request.IntrospectRequest;
import com.chennguyen.garagemanagement.DTO.request.LogoutRequest;
import com.chennguyen.garagemanagement.DTO.request.RefreshRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.AuthenticationResponse;
import com.chennguyen.garagemanagement.DTO.response.IntrospectResponse;
import com.chennguyen.garagemanagement.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication Controller", description = "Quản lý luồng xác thực: Đăng nhập, Đăng xuất, Refresh Token, Kiểm tra Token")
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
            summary = "Đăng nhập hệ thống",
            description = "Nhận vào username/password, trả về Access Token và Refresh Token để sử dụng cho các request sau."
    )
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    @Operation(
            summary = "Kiểm tra hiệu lực Token (Introspect)",
            description = "Kiểm tra xem một Token bất kỳ có hợp lệ (còn hạn, đúng chữ ký) hay không. Thường dùng bởi Resource Server."
    )
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Đăng xuất",
            description = "Vô hiệu hóa Token hiện tại (đưa vào blacklist hoặc xóa khỏi DB) để không thể sử dụng được nữa."
    )
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Làm mới Token (Refresh Token)",
            description = "Khi Access Token hết hạn, dùng Refresh Token để lấy một Access Token mới mà không cần đăng nhập lại."
    )
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}