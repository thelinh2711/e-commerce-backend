package com.example.shop_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shop_backend.model.Address;
import com.example.shop_backend.model.User;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUser(User user);
    Optional<Address> findByIdAndUser(Integer id, User user);
}
