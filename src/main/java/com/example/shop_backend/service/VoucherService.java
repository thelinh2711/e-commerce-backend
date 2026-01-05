package com.example.shop_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.request.CreateVoucherRequest;
import com.example.shop_backend.dto.request.UpdateVoucherRequest;
import com.example.shop_backend.dto.response.VoucherResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.VoucherMapper;
import com.example.shop_backend.model.Voucher;
import com.example.shop_backend.model.enums.StatusVoucher;
import com.example.shop_backend.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    /**
     * Tạo voucher mới
     */
    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {

        if (voucherRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
        }

        try {
            LocalDate startLocalDate = LocalDate.parse(request.getStartDate(), DATE_FORMATTER);
            LocalDate endLocalDate = LocalDate.parse(request.getEndDate(), DATE_FORMATTER);
            
            LocalDateTime startDate = startLocalDate.atStartOfDay(); // 00:00:00
            LocalDateTime endDate = endLocalDate.atTime(23, 59, 59); // 23:59:59

            if (endDate.isBefore(startDate)) {
                throw new AppException(ErrorCode.VOUCHER_INVALID_DATE);
            }

            Voucher voucher = voucherMapper.toVoucher(request);
            voucher.setStartDate(startDate);
            voucher.setEndDate(endDate);
            voucher = voucherRepository.save(voucher);

            return voucherMapper.toVoucherResponse(voucher);
        } catch (DateTimeParseException e) {
            throw new AppException(ErrorCode.VOUCHER_INVALID_DATE);
        }
    }

    /**
     * Cập nhật voucher
     */
    @Transactional
    public VoucherResponse updateVoucher(Integer id, UpdateVoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (request.getCode() != null && !request.getCode().equals(voucher.getCode())) {
            if (voucherRepository.existsByCode(request.getCode())) {
                throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
            }
        }

        LocalDateTime startDate = voucher.getStartDate();
        LocalDateTime endDate = voucher.getEndDate();
        
        try {
            if (request.getStartDate() != null && !request.getStartDate().isBlank()) {
                LocalDate startLocalDate = LocalDate.parse(request.getStartDate(), DATE_FORMATTER);
                startDate = startLocalDate.atStartOfDay(); // 00:00:00
            }
            
            if (request.getEndDate() != null && !request.getEndDate().isBlank()) {
                LocalDate endLocalDate = LocalDate.parse(request.getEndDate(), DATE_FORMATTER);
                endDate = endLocalDate.atTime(23, 59, 59); // 23:59:59
            }
        } catch (DateTimeParseException e) {
            throw new AppException(ErrorCode.VOUCHER_INVALID_DATE);
        }
        
        if (endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.VOUCHER_INVALID_DATE);
        }

        voucherMapper.updateVoucherFromRequest(request, voucher);
        voucher.setStartDate(startDate);
        voucher.setEndDate(endDate);
        voucher = voucherRepository.save(voucher);
        updateStatusVoucher();

        Voucher voucher_new = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return voucherMapper.toVoucherResponse(voucher_new);
    }

    /**
     * Xóa voucher
     */
    @Transactional
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        voucherRepository.delete(voucher);
        updateStatusVoucher();
    }

    /**
     * Lấy thông tin voucher theo ID
     */
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherById(Integer id) {
        updateStatusVoucher();
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        return voucherMapper.toVoucherResponse(voucher);
    }

    /**
     * Lấy voucher theo mã code
     */
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherByCode(String code) {
        updateStatusVoucher();
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        return voucherMapper.toVoucherResponse(voucher);
    }

    /**
     * Lấy tất cả vouchers
     */
    @Transactional(readOnly = true)
    public List<VoucherResponse> getAllVouchers() {
        updateStatusVoucher();
        return voucherRepository.findAll().stream()
                .map(voucherMapper::toVoucherResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy vouchers theo status
     */
    @Transactional(readOnly = true)
    public List<VoucherResponse> getVouchersByStatus(StatusVoucher status) {
        updateStatusVoucher();
        return voucherRepository.findByStatus(status).stream()
                .map(voucherMapper::toVoucherResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật status các voucher
     */
    @Transactional
    public void updateStatusVoucher() {
        List<Voucher> deleteVouchers = voucherRepository.findByStatus(StatusVoucher.INACTIVE);
        for (Voucher voucher : deleteVouchers) {
            voucherRepository.delete(voucher);
        }

        List<Voucher> expiredVouchers = voucherRepository.findExpiredVouchers(LocalDateTime.now());
        for (Voucher voucher : expiredVouchers) {
            voucher.setStatus(StatusVoucher.EXPIRED);
            voucherRepository.save(voucher);
        }

        List<Voucher> outOfStockVouchers = voucherRepository.findOutOfStockVouchers();
        for (Voucher voucher : outOfStockVouchers) {
            voucher.setStatus(StatusVoucher.OUT_OF_STOCK);
            voucherRepository.save(voucher);
        }

        List<Voucher> activeVouchers = voucherRepository.findActiveVouchersUpdate(LocalDateTime.now());
        for (Voucher voucher : activeVouchers) {
            voucher.setStatus(StatusVoucher.ACTIVE);
            voucherRepository.save(voucher);
        }
    }
}
