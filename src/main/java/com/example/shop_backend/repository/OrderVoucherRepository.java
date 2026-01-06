package com.example.shop_backend.repository;

import com.example.shop_backend.model.OrderVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderVoucherRepository
        extends JpaRepository<OrderVoucher, Integer> {

    List<OrderVoucher> findByOrderId(Integer orderId);
}
