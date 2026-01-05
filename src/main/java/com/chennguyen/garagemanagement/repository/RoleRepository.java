package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Tìm kiếm một vai trò (Role) bằng tên của nó.
     * Rất quan trọng để tìm vai trò mặc định (DEFAULT_STAFF_ROLE)
     * hoặc vai trò ADMIN.
     */
    Optional<Role> findByName(String name);
}

