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
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    /**
     * Tạo voucher mới
     * 
     * @param request - Dữ liệu tạo voucher
     * @return VoucherResponse
     * @throws AppException nếu mã voucher đã tồn tại hoặc ngày không hợp lệ
     */
    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        // Kiểm tra mã voucher đã tồn tại
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
        }

        // Parse và set giờ cho startDate và endDate
        try {
            LocalDate startLocalDate = LocalDate.parse(request.getStartDate(), DATE_FORMATTER);
            LocalDate endLocalDate = LocalDate.parse(request.getEndDate(), DATE_FORMATTER);
            
            LocalDateTime startDate = startLocalDate.atStartOfDay(); // 00:00:00
            LocalDateTime endDate = endLocalDate.atTime(23, 59, 59); // 23:59:59

            // Validate ngày
            if (endDate.isBefore(startDate)) {
                throw new AppException(ErrorCode.VOUCHER_INVALID_DATE);
            }

            // Tạo voucher
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
     * 
     * @param id - ID voucher cần update
     * @param request - Dữ liệu cập nhật
     * @return VoucherResponse
     * @throws AppException nếu voucher không tồn tại, mã đã tồn tại hoặc ngày không hợp lệ
     */
    @Transactional
    public VoucherResponse updateVoucher(Integer id, UpdateVoucherRequest request) {
        // Tìm voucher
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        // Kiểm tra mã voucher nếu có thay đổi
        if (request.getCode() != null && !request.getCode().equals(voucher.getCode())) {
            if (voucherRepository.existsByCode(request.getCode())) {
                throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
            }
        }

        // Parse và set giờ cho startDate và endDate nếu có
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
        
        // Validate ngày
        if (endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.VOUCHER_INVALID_DATE);
        }

        // Update voucher
        voucherMapper.updateVoucherFromRequest(request, voucher);
        voucher.setStartDate(startDate);
        voucher.setEndDate(endDate);
        voucher = voucherRepository.save(voucher);

        return voucherMapper.toVoucherResponse(voucher);
    }

    /**
     * Xóa voucher
     * 
     * @param id - ID voucher cần xóa
     * @throws AppException nếu voucher không tồn tại
     */
    @Transactional
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        voucherRepository.delete(voucher);
    }

    /**
     * Lấy thông tin voucher theo ID
     * 
     * @param id - ID voucher
     * @return VoucherResponse
     * @throws AppException nếu voucher không tồn tại
     */
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        return voucherMapper.toVoucherResponse(voucher);
    }

    /**
     * Lấy voucher theo mã code
     * 
     * @param code - Mã voucher
     * @return VoucherResponse
     * @throws AppException nếu voucher không tồn tại
     */
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        return voucherMapper.toVoucherResponse(voucher);
    }

    /**
     * Lấy tất cả vouchers
     * 
     * @return List<VoucherResponse>
     */
    @Transactional(readOnly = true)
    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(voucherMapper::toVoucherResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy vouchers theo status
     * 
     * @param status - Trạng thái voucher
     * @return List<VoucherResponse>
     */
    @Transactional(readOnly = true)
    public List<VoucherResponse> getVouchersByStatus(StatusVoucher status) {
        return voucherRepository.findByStatus(status).stream()
                .map(voucherMapper::toVoucherResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy vouchers đang active (trong thời gian hiệu lực)
     * 
     * @return List<VoucherResponse>
     */
    @Transactional(readOnly = true)
    public List<VoucherResponse> getActiveVouchers() {
        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.findActiveVouchers(StatusVoucher.ACTIVE, now).stream()
                .map(voucherMapper::toVoucherResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật status các voucher đã hết hạn
     * Method này có thể được gọi định kỳ bằng scheduler
     */
    @Transactional
    public void updateExpiredVouchers() {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> expiredVouchers = voucherRepository.findExpiredVouchers(now);
        
        for (Voucher voucher : expiredVouchers) {
            voucher.setStatus(StatusVoucher.EXPIRED);
            voucherRepository.save(voucher);
        }
    }

    /**
     * Validate voucher có thể sử dụng không
     * 
     * @param code - Mã voucher
     * @return VoucherResponse nếu valid
     * @throws AppException nếu voucher không hợp lệ
     */
    @Transactional(readOnly = true)
    public VoucherResponse validateVoucher(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra trạng thái
        if (voucher.getStatus() != StatusVoucher.ACTIVE) {
            throw new AppException(ErrorCode.VOUCHER_NOT_ACTIVE);
        }

        // Kiểm tra thời gian
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }

        // Kiểm tra lượt sử dụng
        if (voucher.getUsageCount() >= voucher.getUsageLimit()) {
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_USES);
        }

        return voucherMapper.toVoucherResponse(voucher);
    }
}
