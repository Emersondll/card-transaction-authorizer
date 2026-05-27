package com.caju.transactionauthorizer.infrastructure.exception;

import com.caju.transactionauthorizer.application.dto.TransactionResponse;
import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TransactionResponse> handleValidationError(MethodArgumentNotValidException ex) {
        return ResponseEntity.ok(new TransactionResponse(AuthorizationStatus.PROCESSING_ERROR.getCode()));
    }
}
