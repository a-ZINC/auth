package com.authms.service.filter;

import com.authms.service.config.AuthServiceProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceKeyFilter extends OncePerRequestFilter {
    private final AuthServiceProperties authServiceProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
            String path = request.getServletPath();
            String method = request.getMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                chain.doFilter(request, response);
                return;
            }

            if (isPublicPath(path)) {
                chain.doFilter(request, response);
                return;
            }

            String serviceKey = request.getHeader("X-Service-Key");

            if (serviceKey == null || serviceKey.isBlank()) {
                log.warn(
                        "Missing X-Service-Key for {} {} from {}",
                        method, path, request.getRemoteAddr()
                );
                writeError(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing X-Service-Key header. "
                                + "This endpoint requires a registered service key."
                );
                return;
            }

            if (!isValidKey(serviceKey)) {
                log.warn(
                        "Invalid X-Service-Key for {} {} from {}",
                        method, path, request.getRemoteAddr()
                );
                writeError(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid X-Service-Key. "
                                + "Please use your registered service key."
                );
                return;
            }

        log.debug("Service key validated for: {}", path);
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return authServiceProperties.getSecurity().getPublicPaths()
                .stream()
                .anyMatch(publicPath -> path.startsWith(publicPath) || path.equals(publicPath));
    }

    private boolean isValidKey(String key) {
        return authServiceProperties.getSecurity()
                .getServiceKeys()
                .contains(key);
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", true);
        body.put("message", message);
        body.put("status", status);
        body.put("timestamp", LocalDateTime.now().toString());

        response.getWriter().write(
                objectMapper.writeValueAsString(body)
        );
    }
}
