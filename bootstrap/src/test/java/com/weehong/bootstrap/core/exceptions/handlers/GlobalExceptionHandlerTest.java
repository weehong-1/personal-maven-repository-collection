package com.weehong.bootstrap.core.exceptions.handlers;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.weehong.bootstrap.core.exceptions.types.BaseException;
import com.weehong.bootstrap.core.exceptions.types.ExternalServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private TestLogger testLogger;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        testLogger = TestLoggerFactory.getTestLogger(GlobalExceptionHandler.class);
        testLogger.clear();
    }

    @AfterEach
    void tearDown() {
        testLogger.clear();
    }

    @Test
    @DisplayName("Should handle BaseException and return ResponseEntity with ProblemDetail")
    void testHandleBaseException_withBaseException_returnsResponseEntityWithProblemDetail() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/123");

        ExternalServiceException exception = new ExternalServiceException(
            "External service failed",
            new RuntimeException("Connection timeout"),
            503,
            "{\"error\": \"unavailable\"}"
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(503);
        assertThat(response.getBody().getDetail()).isEqualTo("External service failed");
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/api/users/123");
    }

    @Test
    @DisplayName("Should handle BaseException without throwing when logging")
    void testHandleBaseException_always_logsError() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/test");

        ExternalServiceException exception = new ExternalServiceException(
            "Service error",
            null,
            500,
            null
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should include error code in ProblemDetail")
    void testHandleBaseException_withErrorCode_includesInProblemDetail() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/resource");

        ExternalServiceException exception = new ExternalServiceException(
            "External error",
            null,
            404,
            null
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties())
            .containsEntry("errorCode", "EXTERNAL_SERVICE_ERROR");
    }

    @Test
    @DisplayName("Should include custom properties in ProblemDetail")
    void testHandleBaseException_withCustomProperties_includesInProblemDetail() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/data");

        ExternalServiceException exception = new ExternalServiceException(
            "Failed to fetch data",
            new RuntimeException("Timeout"),
            500,
            "{\"message\": \"error\"}"
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties())
            .containsEntry("httpStatusCode", 500)
            .containsEntry("responseBody", "{\"message\": \"error\"}")
            .containsEntry("causeType", "RuntimeException");
    }

    @Test
    @DisplayName("Should handle BaseException with BAD_REQUEST status")
    void testHandleBaseException_withBadRequest_returnsCorrectStatus() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/validate");

        TestBaseException exception = new TestBaseException(
            "Validation failed",
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR"
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should handle BaseException with NOT_FOUND status")
    void testHandleBaseException_withNotFound_returnsCorrectStatus() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/999");

        TestBaseException exception = new TestBaseException(
            "User not found",
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND"
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Should handle BaseException with INTERNAL_SERVER_ERROR status")
    void testHandleBaseException_withInternalServerError_returnsCorrectStatus() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/process");

        TestBaseException exception = new TestBaseException(
            "Internal error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR"
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should set correct content type in response")
    void testHandleBaseException_always_setsCorrectContentType() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/test");

        ExternalServiceException exception = new ExternalServiceException(
            "Error",
            null,
            500,
            null
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getType().toString()).isEqualTo("about:blank");
    }

    @Test
    @DisplayName("Should handle exception with cause correctly")
    void testHandleBaseException_always_logsStackTrace() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/error");

        ExternalServiceException exception = new ExternalServiceException(
            "Critical error",
            new RuntimeException("Root cause"),
            500,
            null
        );

        ResponseEntity<ProblemDetail> response = handler.handleBaseException(exception, httpServletRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("Critical error");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return validation errors")
    void testHandleMethodArgumentNotValid_withFieldErrors_returnsValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        WebRequest webRequest = mock(WebRequest.class);

        FieldError fieldError1 = new FieldError("userRequest", "sub", "Sub is required");
        FieldError fieldError2 = new FieldError("userRequest", "email", "Email is invalid");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(exception.getMessage()).thenReturn("Validation failed");

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            exception,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);

        ProblemDetail problemDetail = (ProblemDetail) response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");

        assertThat(problemDetail.getProperties()).containsKey("errors");
        Object errorsObj = problemDetail.getProperties().get("errors");
        assertThat(errorsObj).isInstanceOf(List.class);
        List<?> errors = (List<?>) errorsObj;
        assertThat(errors).hasSize(2);
        assertThat(errors).allSatisfy(error -> assertThat(error).isInstanceOf(Map.class));
        assertThat(errors.toString()).contains("sub", "Sub is required", "email", "Email is invalid");
    }

    @Test
    @DisplayName("Should handle validation errors without throwing")
    void testHandleMethodArgumentNotValid_always_logsWarning() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        WebRequest webRequest = mock(WebRequest.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(exception.getMessage()).thenReturn("Validation error occurred");

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            exception,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest
        );

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private static class TestBaseException extends BaseException {

        TestBaseException(String message, HttpStatus httpStatus, String errorCode) {
            super(message, httpStatus, errorCode);
        }

    }

    @Nested
    @DisplayName("ConstraintViolationException Handler Tests")
    class ConstraintViolationExceptionHandlerTests {

        @Test
        @DisplayName("Should handle ConstraintViolationException and return ResponseEntity with ProblemDetail")
        void testHandleConstraintViolationException_withViolations_returnsResponseEntityWithProblemDetail() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

            ConstraintViolation<?> violation = createMockConstraintViolation("email", "must be a valid email");
            ConstraintViolationException exception = new ConstraintViolationException(
                "Validation failed",
                Set.of(violation)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getDetail()).isEqualTo("Validation failed");
        }

        @Test
        @DisplayName("Should handle ConstraintViolationException without throwing")
        void testHandleConstraintViolationException_always_logsWarning() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/test");

            ConstraintViolation<?> violation = createMockConstraintViolation("field", "is required");
            ConstraintViolationException exception = new ConstraintViolationException(
                "Constraint violation occurred",
                Set.of(violation)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception, httpServletRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should set correct ProblemDetail type, title, and instance")
        void testHandleConstraintViolationException_always_setsCorrectProblemDetailProperties() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/validate");

            ConstraintViolation<?> violation = createMockConstraintViolation("name", "must not be blank");
            ConstraintViolationException exception = new ConstraintViolationException(
                "Validation error",
                Set.of(violation)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getType().toString()).isEqualTo("about:blank");
            assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
            assertThat(response.getBody().getInstance().toString()).isEqualTo("/api/validate");
        }

        @Test
        @DisplayName("Should extract field name from property path without dots")
        void testHandleConstraintViolationException_withSimplePropertyPath_extractsFieldName() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

            ConstraintViolation<?> violation = createMockConstraintViolation("email", "must be a valid email");
            ConstraintViolationException exception = new ConstraintViolationException(
                "Validation failed",
                Set.of(violation)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getProperties()).containsKey("errors");

            List<?> errors = (List<?>) response.getBody().getProperties().get("errors");

            assertThat(errors).hasSize(1);
            Map<?, ?> firstError = (Map<?, ?>) errors.get(0);
            assertThat(firstError.get("field")).isEqualTo("email");
            assertThat(firstError.get("message")).isEqualTo("must be a valid email");
        }

        @Test
        @DisplayName("Should extract field name from property path with dots")
        void testHandleConstraintViolationException_withNestedPropertyPath_extractsLastFieldName() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

            ConstraintViolation<?> violation = createMockConstraintViolation(
                "user.address.zipCode",
                "must be a 5-digit number"
            );
            ConstraintViolationException exception = new ConstraintViolationException(
                "Validation failed",
                Set.of(violation)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getProperties()).containsKey("errors");

            List<?> errors = (List<?>) response.getBody().getProperties().get("errors");

            assertThat(errors).hasSize(1);
            Map<?, ?> firstError = (Map<?, ?>) errors.get(0);
            assertThat(firstError.get("field")).isEqualTo("zipCode");
            assertThat(firstError.get("message")).isEqualTo("must be a 5-digit number");
        }

        @Test
        @DisplayName("Should handle multiple constraint violations")
        void testHandleConstraintViolationException_withMultipleViolations_mapsAllErrors() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/registration");

            ConstraintViolation<?> violation1 = createMockConstraintViolation(
                "email", "must be a valid email");
            ConstraintViolation<?> violation2 = createMockConstraintViolation(
                "password", "must be at least 8 characters");
            ConstraintViolation<?> violation3 = createMockConstraintViolation(
                "user.profile.age", "must be 18 or older");

            ConstraintViolationException exception = new ConstraintViolationException(
                "Multiple validation errors",
                Set.of(violation1, violation2, violation3)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getProperties()).containsKey("errors");

            List<?> errors = (List<?>) response.getBody().getProperties().get("errors");

            assertThat(errors).hasSize(3);

            // Verify all errors are present (order may vary due to Set)
            assertThat(errors).anySatisfy(error -> {
                Map<?, ?> errorMap = (Map<?, ?>) error;
                assertThat(errorMap.get("field")).isEqualTo("email");
                assertThat(errorMap.get("message")).isEqualTo("must be a valid email");
            });

            assertThat(errors).anySatisfy(error -> {
                Map<?, ?> errorMap = (Map<?, ?>) error;
                assertThat(errorMap.get("field")).isEqualTo("password");
                assertThat(errorMap.get("message")).isEqualTo("must be at least 8 characters");
            });

            assertThat(errors).anySatisfy(error -> {
                Map<?, ?> errorMap = (Map<?, ?>) error;
                assertThat(errorMap.get("field")).isEqualTo("age");
                assertThat(errorMap.get("message")).isEqualTo("must be 18 or older");
            });
        }

        @Test
        @DisplayName("Should return HTTP 400 BAD_REQUEST status")
        void testHandleConstraintViolationException_always_returnsBadRequestStatus() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/data");

            ConstraintViolation<?> violation = createMockConstraintViolation("field", "is invalid");
            ConstraintViolationException exception = new ConstraintViolationException(
                "Validation error",
                Set.of(violation)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getStatusCode().value()).isEqualTo(400);
        }

        @Test
        @DisplayName("Should handle empty constraint violations set")
        void testHandleConstraintViolationException_withEmptyViolations_returnsEmptyErrorsList() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/test");

            ConstraintViolationException exception = new ConstraintViolationException(
                "No violations",
                Set.of()
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getProperties()).containsKey("errors");

            List<?> errors = (List<?>) response.getBody().getProperties().get("errors");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should handle property path with multiple dots correctly")
        void testHandleConstraintViolationException_withMultipleDots_extractsLastSegment() {
            when(httpServletRequest.getRequestURI()).thenReturn("/api/complex");

            ConstraintViolation<?> violation = createMockConstraintViolation(
                "organization.department.team.leader.name",
                "must not be empty"
            );
            ConstraintViolationException exception = new ConstraintViolationException(
                "Validation failed",
                Set.of(violation)
            );

            ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(
                exception,
                httpServletRequest
            );

            List<?> errors = (List<?>) response.getBody().getProperties().get("errors");

            assertThat(errors).hasSize(1);
            Map<?, ?> firstError = (Map<?, ?>) errors.get(0);
            assertThat(firstError.get("field")).isEqualTo("name");
        }

        private ConstraintViolation<?> createMockConstraintViolation(String propertyPath, String message) {
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);

            when(path.toString()).thenReturn(propertyPath);
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn(message);

            return violation;
        }

    }

}
