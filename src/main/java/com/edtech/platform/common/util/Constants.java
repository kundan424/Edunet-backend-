package com.edtech.platform.common.util;

public final class Constants {

    private Constants() {}

    // API
    public static final String API_V1 = "/api/v1";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Security
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    // Roles
    public static final String ROLE_STUDENT = "ROLE_STUDENT";
    public static final String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // Video
    public static final int DEFAULT_VIDEO_COMPLETION_THRESHOLD = 95;
}