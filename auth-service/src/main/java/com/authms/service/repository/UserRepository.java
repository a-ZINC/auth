package com.authms.service.repository;

import com.authms.service.entity.User;
import com.authms.service.entity.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("""
        SELECT u FROM User u
        WHERE u.email = :identifier OR u.phone = :identifier
    """)
    Optional<User> findByEmailOrPhone(@Param("identifier") String identifier);

    Optional<User> findByEmailAndEnabledTrue(String email);
    Optional<User> findByPhoneAndEnabledTrue(String phone);

    // For admin queries
    List<User> findAllByEnabledTrue();
    List<User> findAllByEnabledFalse();

    List<User> findAllByCreatedAtAfter(LocalDateTime dateTime);


    @Query("""
        SELECT u FROM User u
        WHERE u.lastLoginAt < :cutoffDate AND u.enabled = true
    """)
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);


    // for update operations
    @Modifying
    @Transactional
    @Query("""
        UPDATE User u
        SET u.lastLoginAt = :lastLoginAt
        WHERE u.id = :userId
    """)
    void updateLastLoginAt(@Param("userId") Long userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);


    @Modifying
    @Transactional
    @Query("""
        UPDATE User u
        SET u.enabled = :enabled
        WHERE u.id = :userId
    """)
    void updateEnabledStatus(@Param("userId") Long userId, @Param("enabled") Boolean enabled);


    // For stats
    long countByEnabledTrue();
    long countByCreatedAtAfter(LocalDateTime dateTime);
}
