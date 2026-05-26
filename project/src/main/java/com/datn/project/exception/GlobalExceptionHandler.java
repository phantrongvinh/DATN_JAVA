package com.datn.project.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<?> handleBadCredentails(BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "status", 401,
                                                "error", "Unauthorized",
                                                "message", "Email hoặc mật khẩu không đúng"));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of(
                                                "status", 403,
                                                "error", "Forbidden",
                                                "message", "You don't have permission to access this resource"));
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<?> handleUnauthorized(AuthenticationException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "status", 401,
                                                "error", "Unauthorized",
                                                "message", ex.getMessage()));
        }

        @ExceptionHandler(ExpiredJwtException.class)
        public ResponseEntity<?> handleExpiredJwt(ExpiredJwtException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "status", 401,
                                                "error", "Token Expired",
                                                "message", "JWT token has expired, please login again"));
        }

        @ExceptionHandler(DisabledException.class)
        public ResponseEntity<?> handleDisabled(
                        DisabledException ex) {
                return ResponseEntity.status(403)
                                .body(Map.of(
                                                "message",
                                                "Account not activated"));
        }
}
