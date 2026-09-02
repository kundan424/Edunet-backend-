package com.edtech.platform.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String code;
    private final String message;
    private final String path;
    private final int status;
    @Builder.Default
    private final Instant timestamp = Instant.now();
    private final String requestId;
    private final Map<String, List<String>> fieldErrors;
}