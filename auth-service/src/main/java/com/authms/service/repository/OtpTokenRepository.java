package com.authms.service.repository;

import com.authms.service.entity.AuthProvider;
import com.authms.service.entity.OtpToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByTokenAndIdentifierAndProvider(String token, String identifier, AuthProvider provider);

    @Query("""
        SELECT ot FROM OtpToken ot
        WHERE ot.token = :token AND ot.provider = :provider AND ot.used = false
    """)
    Optional<OtpToken> findActiveByTokenAndProvider(@Param("token") String token, @Param("provider") AuthProvider provider);

    long countByIdetifierAndProviderAndCreatedAtAfter(String identifier, AuthProvider provider, LocalDateTime after);

    @Modifying
    @Transactional
    void deleteByIdentifierAndProvider(String identifier, AuthProvider provider);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM OtpToken ot
        WHERE ot.expiresAt < :now OR ot.used = true
    """)
    void deleteExpiredAndUsed(@Param("now") LocalDateTime now);

        @Modifying
        @Transactional
        @Query("""
            UPDATE OtpToken ot
            SET ot.used = true
            WHERE ot.id = :id
        """)
        void markAsUsed(@Param("id") Long id);
}
