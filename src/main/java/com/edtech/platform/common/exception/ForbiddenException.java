package com.edtech.platform.common.exception;

public class ForbiddenException extends EdTechException {

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}