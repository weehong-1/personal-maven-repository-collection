package com.weehong.bootstrap.core.exceptions.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalServiceExceptionTest {

    @Test
    @DisplayName("Should create ExternalServiceException with all parameters")
    void testConstructor_withAllParameters_createsCorrectly() {
        RuntimeException cause = new RuntimeException("Connection timeout");
        String responseBody = "{\"error\": \"Service unavailable\"}";

        ExternalServiceException exception = new ExternalServiceException(
            "External service failed",
            cause,
            503,
            responseBody
        );

        assertThat(exception.getMessage()).isEqualTo("External service failed");
        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(exception.getErrorCode()).isEqualTo("EXTERNAL_SERVICE_ERROR");
        assertThat(exception.getCustomProperties())
            .containsEntry("httpStatusCode", 503)
            .containsEntry("responseBody", responseBody)
            .containsEntry("causeType", "RuntimeException");
    }

    @Test
    @DisplayName("Should map status code 404 to NOT_FOUND")
    void testConstructor_withStatusCode404_mapsToNotFound() {
        ExternalServiceException exception = new ExternalServiceException(
            "Resource not found",
            null,
            404,
            null
        );

        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should map status code 400 to BAD_REQUEST")
    void testConstructor_withStatusCode400_mapsToBadRequest() {
        ExternalServiceException exception = new ExternalServiceException(
            "Bad request",
            null,
            400,
            null
        );

        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should map status code 500 to INTERNAL_SERVER_ERROR")
    void testConstructor_withStatusCode500_mapsToInternalServerError() {
        ExternalServiceException exception = new ExternalServiceException(
            "Server error",
            null,
            500,
            null
        );

        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204, 301, 302, 401, 403, 429, 502, 503, 504})
    @DisplayName("Should map valid HTTP status codes correctly")
    void testConstructor_withValidStatusCodes_mapsCorrectly(int statusCode) {
        ExternalServiceException exception = new ExternalServiceException(
            "External service error",
            null,
            statusCode,
            null
        );

        HttpStatus expectedStatus = HttpStatus.resolve(statusCode);
        assertThat(exception.getHttpStatus()).isEqualTo(expectedStatus);
        assertThat(exception.getCustomProperties()).containsEntry("httpStatusCode", statusCode);
    }

    @Test
    @DisplayName("Should default to INTERNAL_SERVER_ERROR when status code is null")
    void testConstructor_withNullStatusCode_defaultsToInternalServerError() {
        ExternalServiceException exception = new ExternalServiceException(
            "Unknown error",
            null,
            null,
            null
        );

        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(exception.getCustomProperties()).doesNotContainKey("httpStatusCode");
    }

    @Test
    @DisplayName("Should default to INTERNAL_SERVER_ERROR when status code is invalid")
    void testConstructor_withInvalidStatusCode_defaultsToInternalServerError() {
        ExternalServiceException exception = new ExternalServiceException(
            "Invalid status",
            null,
            999,
            null
        );

        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should not add responseBody property when response body is null")
    void testConstructor_withNullResponseBody_doesNotAddProperty() {
        ExternalServiceException exception = new ExternalServiceException(
            "Error",
            null,
            500,
            null
        );

        assertThat(exception.getCustomProperties()).doesNotContainKey("responseBody");
    }

    @Test
    @DisplayName("Should not add responseBody property when response body is empty")
    void testConstructor_withEmptyResponseBody_doesNotAddProperty() {
        ExternalServiceException exception = new ExternalServiceException(
            "Error",
            null,
            500,
            ""
        );

        assertThat(exception.getCustomProperties()).doesNotContainKey("responseBody");
    }

    @Test
    @DisplayName("Should add responseBody property when response body is not empty")
    void testConstructor_withNonEmptyResponseBody_addsProperty() {
        String responseBody = "{\"error\": \"details\"}";
        ExternalServiceException exception = new ExternalServiceException(
            "Error",
            null,
            500,
            responseBody
        );

        assertThat(exception.getCustomProperties()).containsEntry("responseBody", responseBody);
    }

    @Test
    @DisplayName("Should not add causeType property when cause is null")
    void testConstructor_withNullCause_doesNotAddCauseType() {
        ExternalServiceException exception = new ExternalServiceException(
            "Error",
            null,
            500,
            null
        );

        assertThat(exception.getCustomProperties()).doesNotContainKey("causeType");
    }

    @Test
    @DisplayName("Should add causeType property when cause is not null")
    void testConstructor_withCause_addsCauseType() {
        RuntimeException cause = new IllegalArgumentException("Invalid parameter");
        ExternalServiceException exception = new ExternalServiceException(
            "Error",
            cause,
            500,
            null
        );

        assertThat(exception.getCustomProperties()).containsEntry("causeType", "IllegalArgumentException");
    }

    @Test
    @DisplayName("Should have correct error code")
    void testConstructor_always_setsCorrectErrorCode() {
        ExternalServiceException exception = new ExternalServiceException(
            "Error",
            null,
            500,
            null
        );

        assertThat(exception.getErrorCode()).isEqualTo("EXTERNAL_SERVICE_ERROR");
    }

    @Test
    @DisplayName("Should include all properties when creating ProblemDetail")
    void testToProblemDetail_withAllProperties_includesEverything() {
        RuntimeException cause = new RuntimeException("Connection error");
        String responseBody = "{\"message\": \"Service unavailable\"}";

        ExternalServiceException exception = new ExternalServiceException(
            "Failed to call external service",
            cause,
            503,
            responseBody
        );

        ProblemDetail problemDetail = exception.toProblemDetail("/api/external");

        assertThat(problemDetail.getStatus()).isEqualTo(503);
        assertThat(problemDetail.getDetail()).isEqualTo("Failed to call external service");
        assertThat(problemDetail.getProperties())
            .containsEntry("errorCode", "EXTERNAL_SERVICE_ERROR")
            .containsEntry("httpStatusCode", 503)
            .containsEntry("responseBody", responseBody)
            .containsEntry("causeType", "RuntimeException");
    }

}
