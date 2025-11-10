package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import com.example.shop_backend.dto.response.ColorResponse;
import com.example.shop_backend.model.Color;

@Mapper(componentModel = "spring")
public interface ColorMapper {
    ColorResponse toResponse(Color color);
}
