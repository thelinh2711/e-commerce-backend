package com.example.shop_backend.repository;

import java.util.Optional;

import com.example.shop_backend.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shop_backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByRole(Role role);

    // =========================
    // OWNER - xem toàn bộ user
    // =========================
    @Query(
            value = """
            SELECT *
            FROM users u
            WHERE 
                (:keyword IS NULL OR 
                    LOWER(u.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND (:role IS NULL OR u.role = :role)
            AND (:status IS NULL OR u.status = :status)
            ORDER BY u.created_at DESC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM users u
            WHERE 
                (:keyword IS NULL OR 
                    LOWER(u.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND (:role IS NULL OR u.role = :role)
            AND (:status IS NULL OR u.status = :status)
            """,
            nativeQuery = true
    )
    Page<User> findAllForOwner(
            @Param("keyword") String keyword,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );

    // =========================
    // ADMIN - chỉ EMPLOYEE + CUSTOMER
    // =========================
    @Query(
            value = """
            SELECT *
            FROM users u
            WHERE 
                (:keyword IS NULL OR 
                    LOWER(u.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND (:role IS NULL OR u.role = :role)
            AND (:status IS NULL OR u.status = :status)
            AND u.role IN ('EMPLOYEE','CUSTOMER')
            ORDER BY u.created_at DESC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM users u
            WHERE 
                (:keyword IS NULL OR 
                    LOWER(u.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND (:role IS NULL OR u.role = :role)
            AND (:status IS NULL OR u.status = :status)
            AND u.role IN ('EMPLOYEE','CUSTOMER')
            """,
            nativeQuery = true
    )
    Page<User> findAllForAdmin(
            @Param("keyword") String keyword,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );
}
