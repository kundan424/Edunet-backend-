package com.edtech.platform.common.exception;

public class InvalidVerificationStateException extends EdTechException {
    public InvalidVerificationStateException(String message) {
        super(ErrorCode.VALIDATION_FAILED, message);
    }
}
