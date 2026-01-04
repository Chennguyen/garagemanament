package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts") // Bảng cho thông tin đăng nhập
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "accountId")
    String id;

    // --- Thông tin Đăng nhập & Bảo mật ---
    @Column(unique = true, nullable = false)
    String username; // Tên đăng nhập

    @Column(nullable = false)
    String password; // Mật khẩu (đã mã hóa)

    @Enumerated(EnumType.STRING)
    Role role;

    // Trạng thái tài khoản (Active/Inactive)
    @Builder.Default
    Boolean enabled = true;
}
