package com.chennguyen.garagemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

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

    // Trạng thái tài khoản
    @Builder.Default
    Boolean enabled = true;


    // 👇 SỬA LẠI: Mỗi tài khoản chỉ gắn với 1 Role duy nhất
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    Role role;
}
