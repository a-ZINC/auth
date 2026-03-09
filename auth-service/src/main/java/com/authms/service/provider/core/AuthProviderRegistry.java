package com.authms.service.provider.core;

import com.authms.service.entity.AuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class AuthProviderRegistry {
    private final Map<AuthProvider, AuthProviderInterface> providers = new HashMap<>();

    public AuthProviderRegistry(List<AuthProviderInterface> providerInterfaceList) {
        for (AuthProviderInterface inter : providerInterfaceList) {
            providers.put(inter.getProvider(), inter);
            log.info(
                    "✅ Registered auth provider: {} ({})",
                    inter.getProvider(),
                    inter.isSingleStep() ? "single-step" : "two-step"
            );
        }
        log.info(
                "Auth provider registry ready with {} providers: {}",
                providers.size(),
                providers.keySet()
        );
    }

    public AuthProviderInterface getProvider(
            AuthProvider providerType
    ) {
        AuthProviderInterface provider =
                providers.get(providerType);

        if (provider == null) {
            throw new IllegalArgumentException(
                    "No auth provider registered for: "
                            + providerType
                            + ". Available providers: "
                            + providers.keySet()
                            + ". To add this provider, implement "
                            + "AuthProviderInterface and annotate "
                            + "with @Component."
            );
        }

        return provider;
    }

    public boolean hasProvider(AuthProvider providerType) {
        return providers.containsKey(providerType);
    }

    public Set<AuthProvider> getRegisteredProviders() {
        return providers.keySet();
    }
}

