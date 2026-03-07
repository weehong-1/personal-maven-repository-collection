package com.weehong.bootstrap.core.exceptions.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class BaseExceptionTest {

    @Test
    @DisplayName("Should create BaseException with all properties")
    void testBaseException_withAllProperties_createsCorrectly() {
        TestException exception = new TestException(
            "Test message",
            HttpStatus.BAD_REQUEST,
            "TEST_ERROR"
        );

        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getErrorCode()).isEqualTo("TEST_ERROR");
        assertThat(exception.getCustomProperties()).isEmpty();
    }

    @Test
    @DisplayName("Should add custom property and return this for chaining")
    void testAddProperty_withKeyValue_addsPropertyAndReturnsThis() {
        TestException exception = new TestException(
            "Test message",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ERROR"
        );

        BaseException result = exception.addProperty("key1", "value1");

        assertThat(result).isSameAs(exception);
        assertThat(exception.getCustomProperties()).containsEntry("key1", "value1");
    }

    @Test
    @DisplayName("Should add multiple custom properties")
    void testAddProperty_withMultipleProperties_addsAllProperties() {
        TestException exception = new TestException(
            "Test message",
            HttpStatus.NOT_FOUND,
            "NOT_FOUND"
        );

        exception.addProperty("key1", "value1")
            .addProperty("key2", 123)
            .addProperty("key3", true);

        assertThat(exception.getCustomProperties())
            .hasSize(3)
            .containsEntry("key1", "value1")
            .containsEntry("key2", 123)
            .containsEntry("key3", true);
    }

    @Test
    @DisplayName("Should create ProblemDetail with all fields")
    void testToProblemDetail_withAllFields_createsProblemDetail() {
        TestException exception = new TestException(
            "Something went wrong",
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR"
        );
        exception.addProperty("field", "email");

        ProblemDetail problemDetail = exception.toProblemDetail("/api/users");

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Something went wrong");
        assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getInstance().toString()).isEqualTo("/api/users");
        assertThat(problemDetail.getProperties())
            .containsEntry("errorCode", "VALIDATION_ERROR")
            .containsEntry("field", "email");
    }

    @Test
    @DisplayName("Should create ProblemDetail without instance when null")
    void testToProblemDetail_withNullInstance_createsWithoutInstance() {
        TestException exception = new TestException(
            "Error message",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "SERVER_ERROR"
        );

        ProblemDetail problemDetail = exception.toProblemDetail(null);

        assertThat(problemDetail.getInstance()).isNull();
        assertThat(problemDetail.getStatus()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should create ProblemDetail without errorCode when null")
    void testToProblemDetail_withNullErrorCode_createsWithoutErrorCode() {
        TestException exception = new TestException(
            "Error message",
            HttpStatus.FORBIDDEN,
            null
        );

        ProblemDetail problemDetail = exception.toProblemDetail("/api/resource");

        if (problemDetail.getProperties() != null) {
            assertThat(problemDetail.getProperties()).doesNotContainKey("errorCode");
        }

        assertThat(problemDetail.getStatus()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should include cause type in ProblemDetail when cause exists")
    void testToProblemDetail_withCause_includesCauseType() {
        RuntimeException cause = new IllegalArgumentException("Invalid input");
        TestException exception = new TestException(
            "Validation failed",
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            cause
        );

        ProblemDetail problemDetail = exception.toProblemDetail("/api/validate");

        assertThat(problemDetail.getProperties())
            .containsEntry("causeType", "IllegalArgumentException");
    }

    @Test
    @DisplayName("Should not include cause type when no cause")
    void testToProblemDetail_withNoCause_doesNotIncludeCauseType() {
        TestException exception = new TestException(
            "Error message",
            HttpStatus.NOT_FOUND,
            "NOT_FOUND"
        );

        ProblemDetail problemDetail = exception.toProblemDetail("/api/resource");

        assertThat(problemDetail.getProperties()).doesNotContainKey("causeType");
    }

    @Test
    @DisplayName("Should include all custom properties in ProblemDetail")
    void testToProblemDetail_withCustomProperties_includesAllProperties() {
        TestException exception = new TestException(
            "Validation error",
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR"
        );
        exception.addProperty("field", "email")
            .addProperty("rejectedValue", "invalid@")
            .addProperty("constraint", "email format");

        ProblemDetail problemDetail = exception.toProblemDetail("/api/users");

        assertThat(problemDetail.getProperties())
            .containsEntry("field", "email")
            .containsEntry("rejectedValue", "invalid@")
            .containsEntry("constraint", "email format")
            .containsEntry("errorCode", "VALIDATION_ERROR");
    }

    @Test
    @DisplayName("Should set type as about:blank in ProblemDetail")
    void testToProblemDetail_always_setsTypeAsAboutBlank() {
        TestException exception = new TestException(
            "Error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ERROR"
        );

        ProblemDetail problemDetail = exception.toProblemDetail("/api/test");

        assertThat(problemDetail.getType().toString()).isEqualTo("about:blank");
    }

    private static class TestException extends BaseException {

        TestException(String message, HttpStatus httpStatus, String errorCode) {
            super(message, httpStatus, errorCode);
        }

        TestException(String message, HttpStatus httpStatus, String errorCode, Throwable cause) {
            super(message, httpStatus, errorCode);
            initCause(cause);
        }

    }

}
