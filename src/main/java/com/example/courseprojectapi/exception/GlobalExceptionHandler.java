package com.example.courseprojectapi.exception;

import com.example.courseprojectapi.model.dto.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                BaseResponse.error(400, "Lỗi dữ liệu", message, req.getRequestURI()), 
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<?> handleAllErrors(Exception ex, HttpServletRequest req) {
        return new ResponseEntity<>(
                BaseResponse.error(500, "Lỗi hệ thống", ex.getMessage(), req.getRequestURI()), 
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
