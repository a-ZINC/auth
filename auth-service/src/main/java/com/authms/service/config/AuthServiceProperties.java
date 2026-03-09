package com.authms.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "authms")
public class AuthServiceProperties {
    private Jwt jwt = new Jwt();
    private Otp otp = new Otp();
    private Email email = new Email();
    private OAuth2 oauth2 = new OAuth2();
    private Security security = new Security();

    @Data
    public static class Jwt {
        private String secretKey = "defaultSecretKeyForJwtSigningChangeThisInProduction";
        private long accessTokenExpirationMs = 15 * 60 * 1000;
        private long refreshTokenExpirationMs = 7 * 24 * 60 * 60;
        private String issuer = "authms";
    }

    @Data
    public static class Otp {
        private int length = 6;
        private long expirationMinutes = 5;
        private int maxRequestPerHour = 5;
    }

    @Data
    public static class Email {
        private String fromAddress = "noreply@myapp.com";
        private String fromName = "MyApp Support";

        private String baseUrl = "http://localhost:8080";

        private int magicLinkExpirationMinutes = 15;
    }

    @Data
    public static class OAuth2 {
        private Google google = new Google();

        @Data
        public static class Google {
            private String clientId = "";
            private String clientSecret = "";
        }
    }

    @Data
    public static class Security {
        private List<String> serviceKeys = List.of(
                "dev-service-key-replace-in-production"
        );
        private List<String> publicPaths = List.of(
                "/auth/initiate",
                "/auth/verify",
                "/auth/refresh",
                "/auth/providers",
                "/auth/health",
                "/actuator/health"
        );
    }
}
