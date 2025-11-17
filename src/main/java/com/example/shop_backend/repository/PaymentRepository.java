package com.example.shop_backend.repository;

import com.example.shop_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByTransactionId(String transactionId);
}
