package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.request.BrandRequest;
import com.example.shop_backend.dto.response.BrandResponse;
import com.example.shop_backend.model.Brand;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand toEntity(BrandRequest request);

    BrandResponse toResponse(Brand brand);

    // update entity từ request (không override id)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBrandFromRequest(BrandRequest request, @MappingTarget Brand brand);
}
