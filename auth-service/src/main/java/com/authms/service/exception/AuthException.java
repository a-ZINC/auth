package com.authms.service.exception;

import org.springframework.http.HttpStatus

public class AuthException extends RuntimeException {
    private final HttpStatus status;

    public AuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static AuthException badRequest(String message) {
        return new AuthException(message, HttpStatus.BAD_REQUEST);
    }

    public static AuthException unauthorized(String message) {
        return new AuthException(message, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException forbidden(String message) {
        return new AuthException(message, HttpStatus.FORBIDDEN);
    }

    public static AuthException notFound(String message) {
        return new AuthException(message, HttpStatus.NOT_FOUND);
    }

    public static AuthException tooManyRequests(String message) {
        return new AuthException(
                message,
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    public HttpStatus getStatus() {
        return status;
    }
}
