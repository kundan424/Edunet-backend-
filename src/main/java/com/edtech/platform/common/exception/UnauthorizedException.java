package com.edtech.platform.common.exception;

public class UnauthorizedException extends EdTechException {

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}