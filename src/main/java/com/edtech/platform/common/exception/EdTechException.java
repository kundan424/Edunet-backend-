package com.edtech.platform.common.exception;

import lombok.Getter;

@Getter
public class EdTechException extends RuntimeException {

    private final ErrorCode errorCode;

    public EdTechException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public EdTechException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EdTechException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}