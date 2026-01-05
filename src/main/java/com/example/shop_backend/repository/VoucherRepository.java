package com.example.shop_backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.Voucher;
import com.example.shop_backend.model.enums.StatusVoucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    
    Optional<Voucher> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Voucher> findByStatus(StatusVoucher status);
    
    @Query("SELECT v FROM Voucher v WHERE v.endDate < :now AND v.status != com.example.shop_backend.model.enums.StatusVoucher.EXPIRED AND v.status != com.example.shop_backend.model.enums.StatusVoucher.INACTIVE")
    List<Voucher> findExpiredVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT v FROM Voucher v WHERE v.usageCount >= v.usageLimit AND v.status != com.example.shop_backend.model.enums.StatusVoucher.OUT_OF_STOCK AND v.status != com.example.shop_backend.model.enums.StatusVoucher.INACTIVE")
    List<Voucher> findOutOfStockVouchers();

    @Query("SELECT v FROM Voucher v WHERE v.startDate <= :now AND v.endDate >= :now AND v.usageCount < v.usageLimit AND v.status != com.example.shop_backend.model.enums.StatusVoucher.ACTIVE AND v.status != com.example.shop_backend.model.enums.StatusVoucher.INACTIVE")
    List<Voucher> findActiveVouchersUpdate(@Param("now") LocalDateTime now);
}
