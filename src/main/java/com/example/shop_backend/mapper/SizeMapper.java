package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import com.example.shop_backend.dto.response.SizeResponse;
import com.example.shop_backend.model.Size;

@Mapper(componentModel = "spring")
public interface SizeMapper {
    SizeResponse toResponse(Size size);
}
