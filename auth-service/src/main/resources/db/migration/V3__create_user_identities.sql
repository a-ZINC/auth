CREATE TABLE userIdentities (
    id BIGSERIAL PRIMARY KEY,
    userId BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL CHECK (provider IN (EMAIL, PHONE, GOOGLE, FACEBOOK, TWITTER, GITHUB, LINKEDIN, APPLE, MICROSOFT, AMAZON, OTHER)),
    providerId VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY userId REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (provider, providerUserId)
);

CREATE INDEX idx_userIdentities_provider_id ON userIdentities (provider, providerId);
CREATE INDEX idx_userIdentities_userId ON userIdentities (userId);

