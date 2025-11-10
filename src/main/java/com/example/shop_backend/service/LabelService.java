package com.example.shop_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.shop_backend.dto.response.LabelResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.LabelMapper;
import com.example.shop_backend.repository.LabelRepository;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public List<LabelResponse> getAll() {
        return labelRepository.findAll()
                .stream()
                .map(labelMapper::toResponse)
                .collect(Collectors.toList());
    }

    public LabelResponse getById(Integer id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LABEL_NOT_FOUND));
        return labelMapper.toResponse(label);
    }
}
