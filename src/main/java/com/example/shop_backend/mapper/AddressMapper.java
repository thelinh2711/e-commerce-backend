package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.request.AddressRequest;
import com.example.shop_backend.dto.response.AddressResponse;
import com.example.shop_backend.model.Address;
import com.example.shop_backend.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    // Request -> Entity (khi tạo mới)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    Address toEntity(AddressRequest request, User user);

    // Entity -> Response
    AddressResponse toResponse(Address address);

    // Cập nhật entity từ request (cho update)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(AddressRequest request, @MappingTarget Address address);
}
