package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import com.example.shop_backend.dto.response.LabelResponse;
import com.example.shop_backend.model.Label;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelResponse toResponse(Label label);
}
