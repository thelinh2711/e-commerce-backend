package com.example.shop_backend.repository;

import com.example.shop_backend.model.User;
import com.example.shop_backend.model.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProviderRepository extends JpaRepository<UserProvider, Integer> {
    Optional<UserProvider> findByProviderNameAndProviderUserId(String providerName, String providerUserId);
    boolean existsByUserAndProviderName(User user, String providerName);
}
