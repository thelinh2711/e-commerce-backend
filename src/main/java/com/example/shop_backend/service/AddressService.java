package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.AddressRequest;
import com.example.shop_backend.dto.response.AddressResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.AddressMapper;
import com.example.shop_backend.model.Address;
import com.example.shop_backend.model.User;
import com.example.shop_backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public List<AddressResponse> getAddresses(User user) {
        return addressRepository.findByUser(user)
                .stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse getAddressById(Integer id, User user) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        return addressMapper.toResponse(address);
    }

    public AddressResponse createAddress(AddressRequest request, User user) {
        Address address = addressMapper.toEntity(request, user);
        addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    public AddressResponse updateAddress(Integer id, AddressRequest request, User user) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        addressMapper.updateEntityFromRequest(request, address);
        addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    public void deleteAddress(Integer id, User user) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        addressRepository.delete(address);
    }
}
