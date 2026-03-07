package com.weehong.bootstrap.core.exceptions.types;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends BaseException {

    public ExternalServiceException(String message,
                                    Throwable cause,
                                    Integer statusCode,
                                    String responseBody) {
        super(message, mapStatusCode(statusCode), "EXTERNAL_SERVICE_ERROR");

        if (cause != null) {
            initCause(cause);
            addProperty("causeType", cause.getClass().getSimpleName());
        }

        if (statusCode != null) {
            addProperty("httpStatusCode", statusCode);
        }

        if (responseBody != null && !responseBody.isEmpty()) {
            addProperty("responseBody", responseBody);
        }
    }

    private static HttpStatus mapStatusCode(Integer statusCode) {
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        HttpStatus resolved = HttpStatus.resolve(statusCode);

        return resolved != null
            ? resolved
            : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}

