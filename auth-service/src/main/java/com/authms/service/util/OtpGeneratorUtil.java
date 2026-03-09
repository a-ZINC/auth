package com.authms.service.util;

import com.authms.service.config.AuthServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class OtpGeneratorUtil {
    private final AuthServiceProperties authServiceProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        int length = authServiceProperties.getOtp().getLength();
        return generateNumeric(length);
    }

    public String generateNumeric(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i=0; i < length; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    public String generateAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder otp = new StringBuilder();
        for (int i=0; i < length; i++) {
            otp.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return otp.toString();
    }
}
