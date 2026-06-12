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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return new ResponseEntity<>(
                BaseResponse.error(404, "Không tìm thấy", ex.getMessage(), req.getRequestURI()), 
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex, HttpServletRequest req) {
        return new ResponseEntity<>(
                BaseResponse.error(ex.getStatus().value(), "Lỗi nghiệp vụ", ex.getMessage(), req.getRequestURI()), 
                ex.getStatus()
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
