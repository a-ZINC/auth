package com.authms.service.provider.core;

import com.authms.service.dto.AuthRequest;
import com.authms.service.dto.AuthResponse;
import com.authms.service.entity.AuthProvider;

public interface AuthProviderInterface {

    AuthProvider getProvider();
    AuthResponse initiate(AuthRequest request);
    AuthResponse verify(AuthRequest request);

    default boolean isSingleStep() {
        return false;
    }

    default String getDisplayName() {
        return getProvider().name();
    }
}
