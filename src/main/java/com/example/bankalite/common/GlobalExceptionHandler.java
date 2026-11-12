package com.example.bankalite.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Validation hatası: @Valid başarısız olunca
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex,
                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ErrorResponse.of(400, message, request.getRequestURI());
    }

    // 2. İş kuralı hatası: kullanıcı zaten var, şifre yanlış vb.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex,
                                                        HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.of(
                ex.getStatus().value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    // 3. Durum hatası: hesap kilitli, rol bulunamadı vb.
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalState(IllegalStateException ex,
                                            HttpServletRequest request) {
        return ErrorResponse.of(409, ex.getMessage(), request.getRequestURI());
    }

    // 4. Beklenmeyen her hata
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception ex,
                                          HttpServletRequest request) {
        // Stack trace loglanır ama istemciye gitmez
        ex.printStackTrace();
        return ErrorResponse.of(500, "Beklenmeyen bir hata oluştu",
                request.getRequestURI());
    }
}