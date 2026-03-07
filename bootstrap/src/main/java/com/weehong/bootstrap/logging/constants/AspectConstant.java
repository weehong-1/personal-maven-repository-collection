package com.weehong.bootstrap.logging.constants;

public final class AspectConstant {

    public static final long SLOW_EXECUTION_THRESHOLD_MS = 1000;

    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_METHOD = "method";
    public static final String MDC_USER_ID = "userId";
    public static final String MDC_HTTP_METHOD = "httpMethod";
    public static final String MDC_ENDPOINT = "endpoint";
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";

    public static final String STATUS_TAG = "status";

    private AspectConstant() {
        throw new AssertionError();
    }

}

