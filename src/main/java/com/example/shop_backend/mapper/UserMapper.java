package com.example.shop_backend.mapper;

import org.mapstruct.*;
import com.example.shop_backend.dto.request.RegisterRequest;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.model.enums.UserStatus;

@Mapper(componentModel = "spring", imports = {Role.class, UserStatus.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // để mã hóa sau
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "role", expression = "java(Role.CUSTOMER)")
    @Mapping(target = "status", expression = "java(UserStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);
}
