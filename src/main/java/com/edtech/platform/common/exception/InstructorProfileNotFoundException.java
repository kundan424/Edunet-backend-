package com.edtech.platform.common.exception;

public class InstructorProfileNotFoundException extends ResourceNotFoundException {
    public InstructorProfileNotFoundException() {
        super("Instructor profile not found");
    }
}
