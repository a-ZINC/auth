package com.authms.service.svc;


import com.authms.service.dto.AuthResponse;
import com.authms.service.entity.*;
import com.authms.service.repository.UserIdentityRepository;
import com.authms.service.repository.UserRepository;
import com.authms.service.repository.UserRoleRepository;
import com.authms.service.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse findOrCreateUser(
            String identifier,
            AuthProvider provider,
            String email,
            String phone,
            String name,
            String avatarUrl,
            String platform
    ) {
        Optional<UserIdentity> existingIdentity =
                userIdentityRepository.findByProviderAndProviderId(provider, identifier);
        if (existingIdentity.isPresent()) {
            User user = existingIdentity.get().getUser();
            Optional<UserRole> existingRole = userRoleRepository.findUserRoleByUserId(user.getId());
            if (existingRole.isPresent()) {
                UserRole role = existingRole.get();
                log.info(
                        "Returning {} user: {}",
                        provider,
                        maskIdentifier(identifier)
                );
                return buildResponse(user, role);
            } else {
                UserRole role = UserRole.builder()
                        .role(Role.GUEST)
                        .userId(user.getId())
                        .platform(platform)
                        .build();
                userRoleRepository.save(role);
                return buildResponse(user, role);
            }
        }


        User user = findExistingUser(email, phone);
        if (user != null) {
            log.info(
                    "Linking {} provider to existing user: {}",
                    provider,
                    maskIdentifier(identifier)
            );
            Optional<UserRole> existingRole = userRoleRepository.findUserRoleByUserId(user.getId());
            if (existingRole.isPresent()) {
                linkIdentity(user, provider, identifier);
                return buildResponse(user, existingRole.get());
            }
        }

        log.info(
                "Creating new user via {}: {}",
                provider,
                maskIdentifier(identifier)
        );
        user = createUserAndRole(email, phone, name, avatarUrl, platform);
        return buildResponse(user, user.getRoles().get(0));
    }

    public AuthResponse buildResponse(User user, UserRole role) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user, role);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return AuthResponse.builder()
                .verified(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Login Successful")
                .user(toUserInfo(user, role))
                .build();
    }

    private AuthResponse.UserInfo toUserInfo(User user, UserRole role) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .name(user.getName())
                .roles(role)
                .build();
    }

    private String maskIdentifier(String identifier) {
        if (identifier == null) return "null";
        if (identifier.contains("@")) {
            int atIndex = identifier.indexOf("@");
            if (atIndex <= 2) return "**" + identifier.substring(atIndex);
            return identifier.substring(0, 2)
                    + "**"
                    + identifier.substring(atIndex);
        }

        if (identifier.length() > 6) {
            return identifier.substring(0, identifier.length() - 6)
                    + "XXXXXX";
        }
        return "****";
    }


    private User findExistingUser(String email, String phone) {
        if (email != null) {
            Optional<User> user = userRepository.findByEmail(email)
            if (user.isPresent()) return user.get();
        }
        if (phone != null) {
            Optional<User> user = userRepository.findByPhone(phone);
            if (user.isPresent()) return user.get();
        }
        return null;
    }

    private User createUserAndRole(String email, String phone, String name, String avatarUrl, String platform) {
        User user = User.builder()
                .email(email)
                .phone(phone)
                .name(name)
                .avatarUrl(avatarUrl)
                .enabled(true)
                .build();
        userRepository.save(user);
        UserRole role = UserRole.builder()
                .userId(user.getId())
                .role(Role.GUEST)
                .platform(platform)
                .user(user)
                .build();

        return user;
    }

    private void linkIdentity(User user, AuthProvider authProvider, String providerId) {
        UserIdentity identity = UserIdentity.builder()
                .userId(user.getId())
                .provider(authProvider)
                .providerId(providerId)
                .verified(true)
                .user(user)
                .build();
        userIdentityRepository.save(identity);
    }


}
