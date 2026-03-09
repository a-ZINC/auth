package com.authms.service.dto;

import com.authms.service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private boolean verified;
    private String accessToken;
    private String refreshToken;
    private String message;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String phone;
        private String name;
        private String avatarUrl;
        private Set<Role> roles;
    }
}
