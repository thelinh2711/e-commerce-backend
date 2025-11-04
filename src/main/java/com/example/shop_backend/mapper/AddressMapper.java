package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.request.AddressRequest;
import com.example.shop_backend.dto.response.AddressResponse;
import com.example.shop_backend.model.Address;
import com.example.shop_backend.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    // Tạo mới Address từ request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "defaultAddress", source = "request.defaultAddress") // ✅ đúng cú pháp
    Address toEntity(AddressRequest request, User user);

    // Map sang response
    AddressResponse toResponse(Address address);

    // Cập nhật thông tin từ request sang entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "defaultAddress", source = "request.defaultAddress") // ✅ đúng cú pháp
    void updateEntityFromRequest(AddressRequest request, @MappingTarget Address address);
}
