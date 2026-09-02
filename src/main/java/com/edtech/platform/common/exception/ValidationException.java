package com.edtech.platform.common.exception;

public class ValidationException extends EdTechException {

    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_FAILED, message);
    }
}