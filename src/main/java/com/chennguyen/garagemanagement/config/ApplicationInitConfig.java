package com.chennguyen.garagemanagement.config;

import com.chennguyen.garagemanagement.entity.Account;
import com.chennguyen.garagemanagement.entity.Role;
import com.chennguyen.garagemanagement.repository.AccountRepository;
import com.chennguyen.garagemanagement.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    static final String ADMIN_USER_NAME = "admin";
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(
            AccountRepository accountRepository,
            RoleRepository roleRepository) {

        log.info("Initializing application data...");

        return args -> {
            // --- 1. Tạo các Roles theo danh sách bạn yêu cầu ---
            log.info("Checking and creating default roles...");

            // Role Quản trị & Quản lý
            Role adminRole = createRoleIfNotFound(roleRepository, "ROLE_ADMIN", "Quản trị viên hệ thống");
            createRoleIfNotFound(roleRepository, "ROLE_MANAGER", "Quản lý gara");

            // Role Khách hàng
            createRoleIfNotFound(roleRepository, "ROLE_CUSTOMER", "Khách hàng");

            // Role Nhân viên
            createRoleIfNotFound(roleRepository, "ROLE_MECHANIC", "Thợ sửa chữa");
            createRoleIfNotFound(roleRepository, "ROLE_CASHIER", "Thu ngân");
            createRoleIfNotFound(roleRepository, "ROLE_SECURITY", "Bảo vệ");

            // --- 2. Tạo tài khoản Admin mặc định (nếu chưa có) ---
            if (accountRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                log.info("Admin user not found. Creating default admin account...");

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole); // Gán quyền ADMIN cao nhất

                Account adminAccount = Account.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .enabled(true)
                        .role(adminRole)
                        .build();

                accountRepository.save(adminAccount);
                log.warn("ADMIN account created successfully with username: '{}'", ADMIN_USER_NAME);
            } else {
                log.info("Admin account already exists.");
            }

            log.info("Application initialization completed!");
        };
    }

    /**
     * Hàm helper: Chỉ tạo Role nếu trong DB chưa có
     */
    private Role createRoleIfNotFound(RoleRepository roleRepository, String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    log.info("Creating new role: {}", name);
                    return roleRepository.save(Role.builder()
                            .name(name)
                            .description(description)
                            .build());
                });
    }
}
