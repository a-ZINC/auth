CREATE TABLE otpTokens (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL CHECK (provider IN (EMAIL, PHONE, GOOGLE, FACEBOOK, TWITTER, GITHUB, LINKEDIN, APPLE, MICROSOFT, AMAZON, OTHER)),
    expiresAt TIMESTAMP NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY userId REFERENCES users(id) ON DELETE CASCADE
);


CREATE INDEX idx_otpTokens_identifier_provider ON otpTokens (identifier, provider);
CREATE INDEX idx_otpTokens_expiresAt ON otpTokens (expiresAt);
CREATE INDEX idx_otp_token ON otpTokens (token);
CREATE INDEX idx_otp_createdAt ON otpTokens (identifier, provider, createdAt);