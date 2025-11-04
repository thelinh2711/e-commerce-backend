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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public AddressResponse createAddress(AddressRequest request, User user) {
        Address address = addressMapper.toEntity(request, user);

        // Nếu địa chỉ này được chọn làm mặc định
        if (request.isDefaultAddress()) {
            // Bỏ mặc định ở tất cả địa chỉ KHÁC của user
            List<Address> addresses = addressRepository.findByUser(user);
            for (Address a : addresses) {
                if (!a.getId().equals(address.getId())) {
                    a.setDefaultAddress(false);
                    addressRepository.save(a);
                }
            }
            address.setDefaultAddress(true);
        } else {
            // Nếu không chọn mặc định, đảm bảo nó không phải default
            address.setDefaultAddress(false);
        }

        Address saved = addressRepository.save(address);
        return addressMapper.toResponse(saved);
    }

    @Transactional
    public AddressResponse updateAddress(Integer id, AddressRequest request, User user) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // Cập nhật thông tin từ request sang entity
        addressMapper.updateEntityFromRequest(request, address);

        if (request.isDefaultAddress()) {
            // Bỏ mặc định ở các địa chỉ KHÁC
            addressRepository.findByUser(user).forEach(a -> {
                if (!a.getId().equals(address.getId())) {
                    a.setDefaultAddress(false);
                    addressRepository.save(a);
                }
            });
            address.setDefaultAddress(true);
        }

        Address saved = addressRepository.save(address);
        return addressMapper.toResponse(saved);
    }


    public void deleteAddress(Integer id, User user) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        addressRepository.delete(address);
    }
}
