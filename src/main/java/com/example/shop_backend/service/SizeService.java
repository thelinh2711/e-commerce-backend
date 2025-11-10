package com.example.shop_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.shop_backend.dto.response.SizeResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.SizeMapper;
import com.example.shop_backend.repository.SizeRepository;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    public List<SizeResponse> getAll() {
        return sizeRepository.findAll()
                .stream()
                .map(sizeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SizeResponse getById(Integer id) {
        var size = sizeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));

        return sizeMapper.toResponse(size);
    }
}
