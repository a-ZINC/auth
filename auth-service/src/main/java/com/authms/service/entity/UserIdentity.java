package com.authms.service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_identities",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "provider"})
        })
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Long userId;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private AuthProvider provider;

        @Column(nullable = false)
        private String providerId;

        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(nullable = false)
        @Builder.Default
        private Boolean verified = false;

        @PrePersist
        protected void onCreate() {
                if (createdAt == null) {
                        createdAt = LocalDateTime.now();
                }
        }
}
