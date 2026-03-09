package com.authms.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth.client")
public class AuthClientProperties {
    private String serviceUrl = "http://localhost:8081";
    private String serviceKey = "dev-service-key-replace-in-production";

    private String jwtSecret = "defaultSecretKeyForJwtSigningChangeThisInProduction";
    private String jwtIssuer = "authms";

    private ValidationMode validationMode = ValidationMode.LOCAL;
    private int timeoutMs = 3000;
    private String[] publicPaths = {};

    public enum ValidationMode {
        LOCAL,
        REMOTE
    }
}
