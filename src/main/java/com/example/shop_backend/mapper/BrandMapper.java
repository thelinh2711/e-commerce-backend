package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.request.BrandRequest;
import com.example.shop_backend.dto.response.BrandResponse;
import com.example.shop_backend.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandResponse toResponse(Brand brand);

    // Bỏ qua field "logo" vì MultipartFile không thể map sang String
    @Mapping(target = "logo", ignore = true)
    Brand toEntity(BrandRequest request);

    // Khi update, cũng bỏ qua logo (xử lý upload riêng trong service)
    @Mapping(target = "logo", ignore = true)
    void updateEntity(@MappingTarget Brand brand, BrandRequest request);
}
