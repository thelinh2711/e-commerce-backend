package com.example.shop_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.shop_backend.dto.response.ColorResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.ColorMapper;
import com.example.shop_backend.repository.ColorRepository;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;

    public List<ColorResponse> getAll() {
        return colorRepository.findAll()
                .stream()
                .map(colorMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ColorResponse getById(Integer id) {
        var color = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

        return colorMapper.toResponse(color);
    }
}
