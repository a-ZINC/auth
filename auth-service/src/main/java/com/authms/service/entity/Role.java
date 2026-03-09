package com.authms.service.entity;

public enum Role {
    SUPER_ADMIN, // Super administrator with all permissions
    ADMIN, // Administrator with elevated permissions
    MODERATOR, // Moderator with permissions to manage content and users
    USER,  // Regular user with basic permissions
    GUEST // Guest user with limited access
}
