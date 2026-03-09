package com.authms.service.repository;

import com.authms.service.entity.AuthProvider;
import com.authms.service.entity.User;
import com.authms.service.entity.UserIdentity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {
     Optional<UserIdentity> findByProviderAndProviderId(AuthProvider provider, String providerId);
     boolean existsByUserIdAndProvider(Long userId, AuthProvider provider);

     Optional<UserIdentity> findByUserIdAndProvider(Long userId, AuthProvider provider);
     List<UserIdentity> findAllByUserId(Long userId);

     @Query("""
         SELECT ui FROM UserIdentity ui
          WHERE ui.userId = :userId and ui.verified = true
     """)
     List<UserIdentity> findAllVerifiedByUserId(Long userId);

     // Update
    @Modifying
    @Transactional
    @Query("""
        UPDATE UserIdentity ui
        SET ui.verified = true
        WHERE ui.userId = :userId AND ui.provider = :provider
    """)
    void updateVerifiedStatus(@Param("userId") Long userId, @Param("provider") AuthProvider provider);

    @Modifying
    @Transactional
    void deleteByUserIdAndProvider(Long userId, AuthProvider provider);

    @Modifying
    @Transactional
    void deleteAllByUserId(Long userId);

     @Modifying
     @Transactional
     @Query("""
         UPDATE UserIdentity ui
         SET ui.providerId = :providerId
         WHERE ui.userId = :userId AND ui.provider = :provider
     """)
     void updateProviderId(@Param("userId") Long userId, @Param("provider") AuthProvider provider, @Param("providerId") String providerId);

     // for admin queries
     @Query("""
         SELECT ui.provider, count(ui) FROM UserIdentity ui
         WHERE ui.verified = true
         GROUP BY ui.provider
     """)
    List<Object[]> countByProvider();

    @Query("""
        SELECT ui.userId FROM UserIdentity ui
        WHERE ui.provider = :provider
          AND ui.verified = true
        """)
    List<Long> findUserIdsByProvider(
            @Param("provider") AuthProvider provider
    );
}
