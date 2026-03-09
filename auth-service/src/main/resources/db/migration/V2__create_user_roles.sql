CREATE TABLE userRoles (
    userId BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN (SUPER_ADMIN, ADMIN, MODERATOR, USER, GUEST)),
    platform VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY userId REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (userId, role)
);

CREATE INDEX idx_userRoles_role ON userRoles (role);
CREATE INDEX idx_userRoles_platform ON userRoles (platform);
CREATE UNIQUE INDEX idx_user_role_platform ON userRoles (userId, role, platform);