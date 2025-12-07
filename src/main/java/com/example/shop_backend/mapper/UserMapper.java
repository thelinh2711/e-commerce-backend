package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.request.CreateUserRequest;
import com.example.shop_backend.dto.request.UpdateStaffRequest;
import com.example.shop_backend.dto.response.StaffResponse;
import org.mapstruct.*;
import com.example.shop_backend.dto.request.RegisterRequest;
import com.example.shop_backend.dto.response.LoginResponse;
import com.example.shop_backend.dto.response.RegisterResponse;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.model.enums.UserStatus;

@Mapper(componentModel = "spring", imports = {Role.class, UserStatus.class})
public interface UserMapper {

    // Map RegisterRequest -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "role", expression = "java(Role.CUSTOMER)")
    @Mapping(target = "status", expression = "java(UserStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    // Map User -> LoginResponse.UserInfo
    @Mapping(target = "email_verified", expression = "java(false)")
    @Mapping(target = "status", expression = "java(user.getStatus().name().toLowerCase())")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "rewardPoints", source = "rewardPoints")
    @Mapping(target = "created_at", source = "createdAt")
    LoginResponse.UserInfo toLoginUserInfo(User user);

    // Map User -> RegisterResponse.UserInfo (dùng lại logic trên)
    @Mapping(target = "email_verified", expression = "java(false)")
    @Mapping(target = "status", expression = "java(user.getStatus().name().toLowerCase())")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "rewardPoints", source = "rewardPoints")
    @Mapping(target = "created_at", source = "createdAt")
    RegisterResponse.UserInfo toRegisterUserInfo(User user);

    // ===== CREATE =====
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rewardPoints", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(CreateUserRequest request);

    // Dùng chung response
    StaffResponse toResponse(User user);

    // ===== UPDATE =====
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateStaff(@MappingTarget User user, UpdateStaffRequest request);

}
