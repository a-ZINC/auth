package com.authms.service.provider.google;


import com.authms.service.config.AuthServiceProperties;
import com.authms.service.dto.AuthRequest;
import com.authms.service.dto.AuthResponse;
import com.authms.service.entity.AuthProvider;
import com.authms.service.provider.core.AuthProviderInterface;
import com.authms.service.svc.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class GoogleAuthProvider implements AuthProviderInterface {
    private final GoogleIdTokenVerifier verifier;
    private final UserService userService;

    public GoogleAuthProvider(AuthServiceProperties authServiceProperties, UserService userService) {
        this.userService = userService;

        String clientId = authServiceProperties.getOauth2().getGoogle().getClientId();

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
                ).setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public boolean isSingleStep() {
        return true;
    }

    @Override
    public AuthResponse initiate(AuthRequest request) {
        if (request.getToken() == null || request.getToken().isBlank()) {
            throw new IllegalArgumentException(
                    "Google ID token is required"
            );
        }

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getToken());
        } catch (Exception e) {
            log.error("Google token verification error: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Google authentication failed. Please try again."
            );
        }

        if (idToken == null) {
            throw new RuntimeException(
                    "Invalid Google token. Please sign in again."
            );
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        String googleId = payload.getSubject();
        String email = payload.getEmail();

        boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String avatarUrl = (String) payload.get("picture");

        if (!emailVerified) {
            throw new RuntimeException(
                    "Google email not verified. "
                            + "Please verify your Google account."
            );
        }

        log.info(
                "Google token verified for: {}**",
                email != null && email.length() > 3
                        ? email.substring(0, 3) : "***"
        );

        return userService.findOrCreateUser(
                googleId,
                AuthProvider.GOOGLE,
                email,
                null,
                name,
                avatarUrl,
                request.getPlatform()
        );
    }

    @Override
    public AuthResponse verify(AuthRequest request) {
        throw new UnsupportedOperationException(
                "Google auth is single-step. Use /auth/initiate only."
        );
    }
}
