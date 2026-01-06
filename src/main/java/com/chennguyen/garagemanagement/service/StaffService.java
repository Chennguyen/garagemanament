package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.StaffRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.request.StaffUpdateRequest;
import com.chennguyen.garagemanagement.DTO.response.StaffResponse;
import com.chennguyen.garagemanagement.emuns.StaffStatus;
import com.chennguyen.garagemanagement.entity.Account;
import com.chennguyen.garagemanagement.entity.Role;
import com.chennguyen.garagemanagement.entity.Staff;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.RoleRepository;
import com.chennguyen.garagemanagement.repository.StaffRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StaffService {
    StaffRepository staffRepository;
    RoleRepository roleRepository;
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;

    @Transactional
    public StaffResponse createStaff(StaffRegistrationRequest request) {
        // 1. Validate Phone
        if (staffRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        // 2. Validate Role
        Role staffRole = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // 3. Validate Facility Code
        String facilityCode = request.getFacilityCode();
        if (facilityCode == null || facilityCode.length() != 2) {
            throw new AppException(ErrorCode.INVALID_FACILITY_CODE); // Code 1009
        }
        Integer currentMax = staffRepository.findMaxIdByFacility(facilityCode);
        int nextSeq = (currentMax == null) ? 1 : currentMax + 1;
        String generatedCode = facilityCode + String.format("%04d", nextSeq); // -> 010001

        // 4. Map DTO -> Staff Entity
        Staff staff = modelMapper.map(request, Staff.class);

        // Set auto fields
        staff.setEmployeeCode(generatedCode);
        staff.setFullName(request.getFullName().toUpperCase());
        staff.setStatus(StaffStatus.ACTIVE);
        if (staff.getHireDate() == null) staff.setHireDate(LocalDate.now());

        // 5. Create Account (Auto)
        Account account = new Account();
        account.setUsername(generatedCode); // User = EmployeeCode
        account.setPassword(passwordEncoder.encode("123")); // Default Pass
        account.setEnabled(true);

        account.setRole(staffRole);

        // Link Account to Staff
        staff.setAccount(account);

        // 6. Save DB
        Staff savedStaff = staffRepository.save(staff);

        // 7. Map Response
        StaffResponse response = modelMapper.map(savedStaff, StaffResponse.class);

        if (savedStaff.getAccount().getRole() != null) {
            response.setRoleName(savedStaff.getAccount().getRole().getName());
            response.setRoleDescription(savedStaff.getAccount().getRole().getDescription());
        }

        return response;
    }

    // --- 2. XEM PROFILE (Dành cho Staff đang login) ---
    public StaffResponse getMyProfile() {
        Staff staff = getCurrentAuthenticatedStaff();
        return convertToResponse(staff);
    }

    // --- 3. UPDATE PROFILE (Dành cho Staff đang login) ---
    @Transactional
    public StaffResponse updateProfile(StaffUpdateRequest request) {
        Staff staff = getCurrentAuthenticatedStaff();

        log.info("Updating profile for staff code: {}", staff.getEmployeeCode());

        // Check từng trường để tránh ghi đè null (Manual Mapping)
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            staff.setFullName(request.getFullName().toUpperCase());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            staff.setAddress(request.getAddress());
        }
        if (request.getGender() != null && !request.getGender().isBlank()) {
            staff.setGender(request.getGender());
        }
        if (request.getDob() != null) {
            staff.setDob(request.getDob());
        }
        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            staff.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null && !request.getBio().isBlank()) {
            staff.setBio(request.getBio());
        }

        Staff updatedStaff = staffRepository.save(staff);
        log.info("Profile updated successfully for staff: {}", updatedStaff.getEmployeeCode());

        return convertToResponse(updatedStaff);
    }

    // ==========================================================
    // 👇 HÀM HELPER LẤY STAFF TỪ TOKEN (Y chang Customer)
    // ==========================================================
    private Staff getCurrentAuthenticatedStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Với Staff, username trong Token chính là Mã Nhân Viên (employeeCode)
        String employeeCode = authentication.getName();

        return staffRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // Helper convert response (Bro nhớ bổ sung các field mới vào StaffResponse nhé)
    private StaffResponse convertToResponse(Staff staff) {
        StaffResponse response = modelMapper.map(staff, StaffResponse.class);

        if (staff.getAccount() != null && staff.getAccount().getRole() != null) {
            response.setRoleName(staff.getAccount().getRole().getName());
            response.setRoleDescription(staff.getAccount().getRole().getDescription());
        }

        // Map thêm SĐT từ Account nếu Entity Staff không lưu SĐT riêng
        if (staff.getAccount() != null) {
            response.setPhoneNumber(staff.getAccount().getUsername()); // Hoặc field username lưu sđt
        }

        return response;
    }
}
