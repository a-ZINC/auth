package com.authms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private String invalidReason;
    private Long userId;
    private String email;
    private String phone;
    private String username;
    private String role;

    private Long expiresAt;

    public static TokenValidationResponse valid(
            Long userId,
            String username,
            String email,
            String phone,
            String role,
            Long expiresAt
    ) {
        return TokenValidationResponse.builder()
                .valid(true)
                .userId(userId)
                .username(username)
                .email(email)
                .phone(phone)
                .role(role)
                .expiresAt(expiresAt)
                .build();
    }

    public static TokenValidationResponse invalid(String reason) {
        return TokenValidationResponse.builder()
                .valid(false)
                .invalidReason(reason)
                .build();
    }

}
