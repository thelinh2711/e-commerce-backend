package com.example.shop_backend.service;

import com.example.shop_backend.model.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.request.CreateUserRequest;
import com.example.shop_backend.dto.request.ResetStaffPasswordRequest;
import com.example.shop_backend.dto.request.UpdateStaffRequest;
import com.example.shop_backend.dto.response.StaffResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.mapper.UserMapper;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.repository.UserRepository;

import static com.example.shop_backend.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OwnerUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ===== Helper: Lấy user hiện tại =====
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // ===== Helper: Kiểm tra quyền quản lý =====
    private boolean canManage(Role currentRole, Role targetRole) {

        if (currentRole == Role.OWNER) {
            return true; // OWNER quản lý tất cả
        }

        if (currentRole == Role.ADMIN) {
            return targetRole == Role.EMPLOYEE || targetRole == Role.CUSTOMER;
        }

        return false;
    }

    // 1. Tạo tài khoản (ADMIN / EMPLOYEE / CUSTOMER)
    @Transactional
    public StaffResponse createUser(CreateUserRequest request) {

        User currentUser = getCurrentUser();

        boolean canCreate =
                (currentUser.getRole() == Role.OWNER) ||
                        (currentUser.getRole() == Role.ADMIN && request.getRole() == Role.EMPLOYEE);

        if (!canCreate) {
            throw new AppException(ROLE_NOT_ALLOWED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(PHONE_ALREADY_EXISTS);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(PASSWORD_NOT_MATCH);
        }

        User user = userMapper.toEntity(request);
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toResponse(userRepository.save(user));
    }


    // 2. Cập nhật / đóng băng tài khoản
    @Transactional
    public StaffResponse updateUser(Integer id, UpdateStaffRequest request) {

        User currentUser = getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        // 1. Check quyền quản lý
        if (!canManage(currentUser.getRole(), targetUser.getRole())) {
            throw new AppException(ROLE_NOT_ALLOWED);
        }

        // 2. Check trùng số điện thoại
        if (request.getPhone() != null &&
                userRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new AppException(PHONE_ALREADY_EXISTS);
        }

        // 3. Update cơ bản (fullName, phone)
        userMapper.updateStaff(targetUser, request);

        // 4. OWNER được set role
        if (request.getRole() != null) {
            if (currentUser.getRole() != Role.OWNER) {
                throw new AppException(ROLE_NOT_ALLOWED);
            }

            // Không cho hạ OWNER
            if (targetUser.getRole() == Role.OWNER) {
                throw new AppException(ROLE_NOT_ALLOWED);
            }

            targetUser.setRole(request.getRole());
        }

        // 5. OWNER + ADMIN được set status
        if (request.getStatus() != null) {

            boolean canSetStatus =
                    currentUser.getRole() == Role.OWNER ||
                            (currentUser.getRole() == Role.ADMIN &&
                                    (targetUser.getRole() == Role.EMPLOYEE ||
                                            targetUser.getRole() == Role.CUSTOMER));

            if (!canSetStatus) {
                throw new AppException(ROLE_NOT_ALLOWED);
            }

            targetUser.setStatus(request.getStatus());
        }

        return userMapper.toResponse(userRepository.save(targetUser));
    }

    // 3. Reset mật khẩu
    @Transactional
    public void resetPassword(Integer id, ResetStaffPasswordRequest request) {

        User currentUser = getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        // Rule phân quyền reset password
        boolean canReset =
                (currentUser.getRole() == Role.OWNER &&
                        (targetUser.getRole() == Role.ADMIN ||
                                targetUser.getRole() == Role.EMPLOYEE))
                        ||
                        (currentUser.getRole() == Role.ADMIN &&
                                targetUser.getRole() == Role.EMPLOYEE);

        if (!canReset) {
            throw new AppException(ROLE_NOT_ALLOWED);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(PASSWORD_NOT_MATCH);
        }

        targetUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(targetUser);
    }

    @Transactional(readOnly = true)
    public Page<StaffResponse> getUsers(
            String keyword,
            Role role,
            UserStatus status,
            int page,
            int size) {

        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<User> users;

        if (currentUser.getRole() == Role.OWNER) {
            users = userRepository.findAllForOwner(
                    keyword,
                    role != null ? role.name() : null,
                    status != null ? status.name() : null,
                    pageable
            );
        }
        else if (currentUser.getRole() == Role.ADMIN) {
            users = userRepository.findAllForAdmin(
                    keyword,
                    role != null ? role.name() : null,
                    status != null ? status.name() : null,
                    pageable
            );
        }
        else {
            throw new AppException(ROLE_NOT_ALLOWED);
        }

        return users.map(userMapper::toResponse);
    }

}
