package com.weehong.bootstrap.logging.exceptions;

public class MethodExecutionException extends RuntimeException {

    public MethodExecutionException(String methodName,
                                    long executionTimeMs,
                                    Throwable cause) {
        super(
            String.format("Execution failed for method %s after %d ms", methodName, executionTimeMs),
            cause
        );
    }

}
