package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.shop_backend.dto.request.CreateVoucherRequest;
import com.example.shop_backend.dto.request.UpdateVoucherRequest;
import com.example.shop_backend.dto.response.VoucherResponse;
import com.example.shop_backend.model.Voucher;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VoucherMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", constant = "0")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    Voucher toVoucher(CreateVoucherRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    void updateVoucherFromRequest(UpdateVoucherRequest request, @MappingTarget Voucher voucher);
    
    VoucherResponse toVoucherResponse(Voucher voucher);
}
