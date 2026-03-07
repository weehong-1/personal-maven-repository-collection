package com.weehong.bootstrap.core.exceptions.types;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final transient Map<String, Object> customProperties;

    protected BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.customProperties = new HashMap<>();
    }

    public final BaseException addProperty(String key, Object value) {
        this.customProperties.put(key, value);

        return this;
    }

    public final ProblemDetail toProblemDetail(String instance) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, getMessage());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle(httpStatus.getReasonPhrase());

        if (instance != null) {
            problemDetail.setInstance(URI.create(instance));
        }

        if (errorCode != null) {
            problemDetail.setProperty("errorCode", errorCode);
        }

        if (getCause() != null) {
            problemDetail.setProperty("causeType", getCause().getClass().getSimpleName());
        }

        customProperties.forEach(problemDetail::setProperty);

        return problemDetail;
    }

}

