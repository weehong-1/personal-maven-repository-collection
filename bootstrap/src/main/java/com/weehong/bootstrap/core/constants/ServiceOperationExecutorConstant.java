package com.weehong.bootstrap.core.constants;

public final class ServiceOperationExecutorConstant {

    public static final String TRUNCATION_INDICATOR = "\n\n--- RESPONSE TRUNCATED (exceeded 10KB limit) ---";
    public static final String MDC_REQUEST_ID_KEY = "requestId";
    public static final int MAX_RESPONSE_BODY_SIZE = 10_000;

    private ServiceOperationExecutorConstant() {
    }

}
