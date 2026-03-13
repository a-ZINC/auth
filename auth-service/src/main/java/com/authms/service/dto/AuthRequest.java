package com.authms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String provider;
    private String email;
    private String phone;
    private String token;
    private String otp;
    private String name;
    private String platform;

}
