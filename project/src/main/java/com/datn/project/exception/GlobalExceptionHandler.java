package com.datn.project.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
                                                "message", "Bạn không có quyền truy cập vào trang này"));
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
                                                "message", "Mã đăng nhập đã hết hạng, vui lòng đăng nhập lại"));
        }

        @ExceptionHandler(LockedException.class)
        public ResponseEntity<?> handleLocked(LockedException ex) {
                return ResponseEntity.status(403)
                                .body(Map.of(
                                                "message",
                                                "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ bộ phận hỗ trợ để được kích hoạt lại.",
                                                "reason", "ACCOUNT_DISABLED"));
        }

        @ExceptionHandler(DisabledException.class)
        public ResponseEntity<?> handleDisabled(
                        DisabledException ex) {
                return ResponseEntity.status(403)
                                .body(Map.of(
                                                "message",
                                                "Tài khoản chưa được kích hoạt, hãy gửi lại mã kích hoạt"));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<?> handleRuntime(RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<?> handleNotFound(BadRequestException be) {
                return ResponseEntity.status(404).body(Map.of("message", be.getMessage()));
        }

        // GlobalExceptionHandler.java
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<?> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "File upload quá lớn. Vui lòng chọn file nhỏ hơn 10MB"));
        }

}
