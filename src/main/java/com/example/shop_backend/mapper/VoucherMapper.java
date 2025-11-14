package com.example.shop_backend.mapper;

import java.time.LocalDateTime;

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
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    void updateVoucherFromRequest(UpdateVoucherRequest request, @MappingTarget Voucher voucher);
    
    @Mapping(target = "isActive", expression = "java(calculateIsActive(voucher))")
    @Mapping(target = "isExpired", expression = "java(calculateIsExpired(voucher))")
    @Mapping(target = "remainingUses", expression = "java(calculateRemainingUses(voucher))")
    VoucherResponse toVoucherResponse(Voucher voucher);
    
    default Boolean calculateIsActive(Voucher voucher) {
        LocalDateTime now = LocalDateTime.now();
        return voucher.getStatus() == com.example.shop_backend.model.enums.StatusVoucher.ACTIVE
                && voucher.getStartDate().isBefore(now)
                && voucher.getEndDate().isAfter(now)
                && voucher.getUsageCount() < voucher.getUsageLimit();
    }
    
    default Boolean calculateIsExpired(Voucher voucher) {
        return voucher.getEndDate().isBefore(LocalDateTime.now())
                || voucher.getStatus() == com.example.shop_backend.model.enums.StatusVoucher.EXPIRED;
    }
    
    default Integer calculateRemainingUses(Voucher voucher) {
        return Math.max(0, voucher.getUsageLimit() - voucher.getUsageCount());
    }
}
