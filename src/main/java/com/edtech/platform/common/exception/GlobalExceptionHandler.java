package com.edtech.platform.common.exception;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.response.ErrorResponse;
import com.edtech.platform.common.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EdTechException.class)
    public ResponseEntity<ApiResponse<Void>> handleEdTechException(
            EdTechException ex, HttpServletRequest request) {

        ErrorCode errorCode = ex.getErrorCode();
        log.warn("EdTech business exception [{}]: {}", errorCode.getCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .status(errorCode.getHttpStatus().value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, List<String>> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors
                    .computeIfAbsent(fieldError.getField(), k -> new ArrayList<>())
                    .add(fieldError.getDefaultMessage());
        }

        log.warn("Validation failed for request {}: {}", request.getRequestURI(), fieldErrors);

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message("Validation failed. Check field errors.")
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied to {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.FORBIDDEN.getCode())
                .message("Access denied")
                .path(request.getRequestURI())
                .status(HttpStatus.FORBIDDEN.value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.INVALID_CREDENTIALS.getCode())
                .message(ErrorCode.INVALID_CREDENTIALS.getDefaultMessage())
                .path(request.getRequestURI())
                .status(HttpStatus.UNAUTHORIZED.value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("Malformed request body: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message("Malformed or missing request body")
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .build();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message("Invalid parameter value: " + ex.getName())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .build();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.RESOURCE_NOT_FOUND.getCode())
                .message("The requested endpoint does not exist")
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception for request {}", request.getRequestURI(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .requestId(MDC.get(Constants.REQUEST_ID_MDC_KEY))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }
}