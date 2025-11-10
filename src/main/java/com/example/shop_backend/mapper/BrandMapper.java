package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.request.BrandRequest;
import com.example.shop_backend.dto.response.BrandResponse;
import com.example.shop_backend.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandResponse toResponse(Brand brand);

    Brand toEntity(BrandRequest request);

    void updateEntity(@MappingTarget Brand brand, BrandRequest request);
}
