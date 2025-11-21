package com.example.shop_backend.repository;

import com.example.shop_backend.model.Payment;
import com.example.shop_backend.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByTransactionId(String transactionId);
    
    // Tìm tất cả payment theo status
    List<Payment> findByStatus(PaymentStatus status);
}