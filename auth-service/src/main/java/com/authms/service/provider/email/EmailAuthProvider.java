package com.authms.service.provider.email;

import com.authms.service.config.AuthServiceProperties;
import com.authms.service.dto.AuthRequest;
import com.authms.service.dto.AuthResponse;
import com.authms.service.entity.AuthProvider;
import com.authms.service.entity.OtpToken;
import com.authms.service.provider.core.AuthProviderInterface;
import com.authms.service.repository.OtpTokenRepository;
import com.authms.service.svc.EmailService;
import com.authms.service.svc.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAuthProvider implements AuthProviderInterface {

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private  final UserService userService;
    private final AuthServiceProperties authServiceProperties;

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.EMAIL;
    }

    @Override
    @Transactional
    public AuthResponse initiate(AuthRequest authRequest) {
        String email = validate(authRequest.getEmail(), "Email");
        email = email.toLowerCase().trim();

        otpTokenRepository.deleteByIdentifierAndProvider(email, AuthProvider.EMAIL);

        String token = UUID.randomUUID().toString();

        int expiryMins = authServiceProperties.getOtp().getExpirationMinutes();

        otpTokenRepository.save(
                OtpToken.builder()
                        .identifier(email)
                        .provider(AuthProvider.EMAIL)
                        .token(token)
                        .expiresAt(LocalDateTime.now().plusMinutes(expiryMins))
                        .used(false)
                        .build()
        );

        emailService.sendMagicLink(email, token);

        log.info("Magic link initiated for: {}**", email.substring(0, 3));

        return AuthResponse.builder()
                .verified(false)
                .message(
                        "Magic link sent to " + email
                                + ". Expires in " + expiryMins + " minutes."
                )
                .build();
    }

    @Override
    @Transactional
    public AuthResponse verify(AuthRequest authRequest) {
        String token = validate(authRequest.getToken(), "Token");

        OtpToken otpToken = otpTokenRepository
                .findActiveByTokenAndProvider(token, AuthProvider.EMAIL)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid or expired magic link. "
                                        + "Please request a new one."
                        )
                );
        if (otpToken.isExpired()) {
            throw new RuntimeException(
                    "Magic link has expired. Please request a new one."
            );
        }

        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        String email = otpToken.getIdentifier();
        log.info("Email verified: {}**", email.substring(0, 3));

        return userService.findOrCreateUser(email, AuthProvider.EMAIL, email, null, authRequest.getName(), null, authRequest.getPlatform());
    }

    private String validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }
        return value;
    }
}
