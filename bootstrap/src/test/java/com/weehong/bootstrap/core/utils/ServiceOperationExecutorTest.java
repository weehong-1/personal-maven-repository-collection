package com.weehong.bootstrap.core.utils;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.weehong.bootstrap.core.enums.OperationStatus;
import com.weehong.bootstrap.core.exceptions.types.ExceptionFactory;
import com.weehong.bootstrap.core.exceptions.types.ExternalServiceException;
import com.weehong.bootstrap.core.exceptions.types.InvalidFileException;
import com.weehong.bootstrap.core.exceptions.types.ResourceNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.MDC;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import static com.weehong.bootstrap.core.constants.ServiceOperationExecutorConstant.MDC_REQUEST_ID_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceOperationExecutorTest {

    private TestLogger testLogger;
    private ExceptionFactory exceptionFactory;

    @BeforeEach
    void setUp() {
        testLogger = TestLoggerFactory.getTestLogger(ServiceOperationExecutor.class);
        testLogger.clear();
        MDC.clear();
        exceptionFactory = RuntimeException::new;
    }

    @AfterEach
    void tearDown() {
        testLogger.clear();
        MDC.clear();
    }

    @Test
    @DisplayName("Should throw UnsupportedOperationException when trying to instantiate")
    void testConstructor_throwsException() {
        assertThatThrownBy(() -> {
            Constructor<?> constructor = ServiceOperationExecutor.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        })
            .isInstanceOf(InvocationTargetException.class)
            .hasCauseInstanceOf(UnsupportedOperationException.class)
            .cause()
            .hasMessageContaining("Cannot instantiate utility class");
    }

    @Test
    @DisplayName("Should execute operation successfully and return result")
    void testExecute_withSuccessfulOperation_returnsResult() {
        String expectedResult = "success";

        String result = ServiceOperationExecutor.execute(
            () -> expectedResult,
            OperationStatus.RETRIEVE,
            exceptionFactory
        );

        assertThat(result).isEqualTo(expectedResult);
        assertThat(MDC.get(MDC_REQUEST_ID_KEY)).isNotNull();
    }

    @Test
    @DisplayName("Should create and use trace ID from MDC if not exists")
    void testExecute_withNoTraceId_createsNewTraceId() {
        ServiceOperationExecutor.execute(
            () -> "test",
            OperationStatus.RETRIEVE,
            exceptionFactory
        );

        assertThat(MDC.get(MDC_REQUEST_ID_KEY)).isNotNull();
    }

    @Test
    @DisplayName("Should use existing trace ID from MDC")
    void testExecute_withExistingTraceId_usesExistingTraceId() {
        String existingTraceId = "existing-trace-id";
        MDC.put(MDC_REQUEST_ID_KEY, existingTraceId);

        ServiceOperationExecutor.execute(
            () -> "test",
            OperationStatus.RETRIEVE,
            exceptionFactory
        );

        assertThat(MDC.get(MDC_REQUEST_ID_KEY)).isEqualTo(existingTraceId);
    }

    @Test
    @DisplayName("Should throw NullPointerException when operation is null")
    void testExecute_withNullOperation_throwsNullPointerException() {
        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(null, OperationStatus.RETRIEVE, exceptionFactory)
        ).isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Operation supplier must not be null");
    }

    @Test
    @DisplayName("Should throw NullPointerException when exception factory is null")
    void testExecute_withNullExceptionFactory_throwsNullPointerException() {
        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(() -> "test", OperationStatus.RETRIEVE, null)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should rethrow IllegalArgumentException without wrapping")
    void testExecute_withIllegalArgumentException_rethrowsException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isSameAs(exception);
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException correctly")
    void testExecute_withResourceNotFoundException_rethrowsException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isSameAs(exception);
    }

    @Test
    @DisplayName("Should wrap InvalidFileException with custom exception")
    void testExecute_withInvalidFileException_wrapsException() {
        InvalidFileException exception = new InvalidFileException("Invalid file", new RuntimeException());

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.OTHER,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid file");
    }

    @Test
    @DisplayName("Should handle OptimisticLockingFailureException")
    void testExecute_withOptimisticLockingFailure_createsCustomException() {
        OptimisticLockingFailureException exception = new OptimisticLockingFailureException("Optimistic lock");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.UPDATE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Concurrent modification detected");
    }

    @Test
    @DisplayName("Should handle PessimisticLockingFailureException")
    void testExecute_withPessimisticLockingFailure_createsCustomException() {
        PessimisticLockingFailureException exception = new PessimisticLockingFailureException("Pessimistic lock");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.UPDATE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Resource is locked by another transaction");
    }

    @Test
    @DisplayName("Should handle CannotAcquireLockException")
    void testExecute_withCannotAcquireLock_createsCustomException() {
        CannotAcquireLockException exception = new CannotAcquireLockException("Cannot acquire lock");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.UPDATE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Cannot acquire database lock");
    }

    @Test
    @DisplayName("Should handle PessimisticLockingFailureException for deadlock scenario")
    void testExecute_withDeadlockScenario_createsCustomException() {
        PessimisticLockingFailureException exception = new PessimisticLockingFailureException("Deadlock detected");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Resource is locked by another transaction");
    }

    @Test
    @DisplayName("Should handle DeadlockLoserDataAccessException with retry message")
    void testExecute_withDeadlockLoserDataAccessException_createsDeadlockMessage() {
        DeadlockLoserDataAccessException exception = new DeadlockLoserDataAccessException(
            "Deadlock found when trying to get lock",
            null
        );

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.UPDATE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Database deadlock detected - please retry the operation");
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException for RETRIEVE operation")
    void testExecute_withEntityNotFoundOnRetrieve_createsCustomException() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("Entity not found");
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException for UPDATE operation")
    void testExecute_withEntityNotFoundOnUpdate_createsCustomException() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.UPDATE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Entity does not exist or was deleted - cannot update");
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException for DELETE operation")
    void testExecute_withEntityNotFoundOnDelete_createsCustomException() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.DELETE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Entity does not exist or was already deleted");
    }

    @Test
    @DisplayName("Should handle EntityExistsException")
    void testExecute_withEntityExists_createsCustomException() {
        EntityExistsException exception = new EntityExistsException("Entity exists");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("Entity already exists");
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException for SAVE operation")
    void testExecute_withDataIntegrityViolationOnSave_createsCustomException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Integrity violation");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Data integrity violation")
            .hasMessageContaining("duplicate entry or constraint failure");
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException for DELETE operation")
    void testExecute_withDataIntegrityViolationOnDelete_createsCustomException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Integrity violation");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.DELETE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Data integrity violation")
            .hasMessageContaining("cannot delete due to foreign key constraint");
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException for SAVE operation")
    void testExecute_withConstraintViolationOnSave_createsCustomException() {
        ConstraintViolationException exception = new ConstraintViolationException(
            "Constraint violation",
            new SQLException(),
            "constraint"
        );

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Constraint violation")
            .hasMessageContaining("entity fails validation rules");
    }

    @Test
    @DisplayName("Should handle InvalidDataAccessResourceUsageException with table not found")
    void testExecute_withTableNotFound_createsCustomException() {
        InvalidDataAccessResourceUsageException exception = new InvalidDataAccessResourceUsageException(
            "Error",
            new RuntimeException("Table 'users' doesn't exist")
        );

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Database table does not exist");
    }

    @Test
    @DisplayName("Should handle InvalidDataAccessResourceUsageException with column not found")
    void testExecute_withColumnNotFound_createsCustomException() {
        InvalidDataAccessResourceUsageException exception = new InvalidDataAccessResourceUsageException(
            "Error",
            new RuntimeException("Unknown column 'email' in 'field list'")
        );

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Database column mismatch detected");
    }

    @Test
    @DisplayName("Should handle JpaSystemException")
    void testExecute_withJpaSystemException_createsCustomException() {
        JpaSystemException exception = new JpaSystemException(new RuntimeException("JPA error"));

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("JPA system error occurred");
    }

    @Test
    @DisplayName("Should handle InvalidDataAccessApiUsageException")
    void testExecute_withInvalidDataAccessApiUsage_createsCustomException() {
        InvalidDataAccessApiUsageException exception = new InvalidDataAccessApiUsageException("Invalid API usage");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid usage of the Data Access API");
    }

    @Test
    @DisplayName("Should handle PersistenceException")
    void testExecute_withPersistenceException_createsCustomException() {
        PersistenceException exception = new PersistenceException("Persistence error");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("Persistence error occurred");
    }

    @Test
    @DisplayName("Should handle HttpServerErrorException and create ExternalServiceException")
    void testExecute_withHttpServerErrorException_createsExternalServiceException() {
        HttpServerErrorException exception = new HttpServerErrorException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Server Error"
        );

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining("External service error during retrieve");
    }

    @Test
    @DisplayName("Should handle HttpServerErrorException with response body")
    void testExecute_withHttpServerErrorExceptionAndBody_createsExternalServiceExceptionWithBody() {
        String responseBody = "{\"error\": \"Internal Server Error\"}";
        HttpServerErrorException exception = HttpServerErrorException.create(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Server Error",
            null,
            responseBody.getBytes(),
            null
        );

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining("External service error during save")
            .satisfies(ex -> {
                ExternalServiceException ese = (ExternalServiceException) ex;
                assertThat(ese.getCustomProperties()).containsEntry("responseBody", responseBody);
                assertThat(ese.getCustomProperties()).containsEntry("httpStatusCode", 500);
            });
    }

    @Test
    @DisplayName("Should truncate large response body")
    void testExecute_withLargeResponseBody_truncatesBody() {
        String largeResponseBody = "x".repeat(15000);
        HttpServerErrorException exception = HttpServerErrorException.create(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Server Error",
            null,
            largeResponseBody.getBytes(),
            null
        );

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isInstanceOf(ExternalServiceException.class)
            .satisfies(ex -> {
                ExternalServiceException ese = (ExternalServiceException) ex;
                String body = (String) ese.getCustomProperties().get("responseBody");
                assertThat(body).isNotNull();
                assertThat(body.length()).isLessThan(largeResponseBody.length());
                assertThat(body).contains("RESPONSE TRUNCATED");
            });
    }

    @Test
    @DisplayName("Should handle ResourceAccessException")
    void testExecute_withResourceAccessException_createsExternalServiceException() {
        ResourceAccessException exception = new ResourceAccessException("Service unavailable");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.RETRIEVE,
                exceptionFactory
            )
        ).isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining("External service unavailable during retrieve");
    }

    @Test
    @DisplayName("Should bubble up programmer errors - NullPointerException")
    void testExecute_withNullPointerException_bubblesUp() {
        NullPointerException exception = new NullPointerException("Null pointer");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isSameAs(exception);
    }

    @Test
    @DisplayName("Should bubble up programmer errors - IllegalStateException")
    void testExecute_withIllegalStateException_bubblesUp() {
        IllegalStateException exception = new IllegalStateException("Illegal state");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isSameAs(exception);
    }

    @Test
    @DisplayName("Should bubble up programmer errors - ClassCastException")
    void testExecute_withClassCastException_bubblesUp() {
        ClassCastException exception = new ClassCastException("Class cast");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isSameAs(exception);
    }

    @Test
    @DisplayName("Should wrap unexpected RuntimeException")
    void testExecute_withUnexpectedRuntimeException_wrapsException() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        assertThatThrownBy(() ->
            ServiceOperationExecutor.execute(
                () -> {
                    throw exception;
                },
                OperationStatus.SAVE,
                exceptionFactory
            )
        ).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Unexpected error occurred during save");
    }

    @Test
    @DisplayName("Should execute void operation successfully")
    void testExecuteVoid_withSuccessfulOperation_executesWithoutError() {
        final boolean[] executed = {false};

        ServiceOperationExecutor.executeVoid(
            () -> executed[0] = true,
            OperationStatus.DELETE,
            exceptionFactory
        );

        assertThat(executed[0]).isTrue();
    }

    @Test
    @DisplayName("Should handle exception in void operation")
    void testExecuteVoid_withException_throwsException() {
        assertThatThrownBy(() ->
            ServiceOperationExecutor.executeVoid(
                () -> {
                    throw new IllegalArgumentException("Error");
                },
                OperationStatus.DELETE,
                exceptionFactory
            )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @EnumSource(OperationStatus.class)
    @DisplayName("Should handle all operation types")
    void testExecute_withAllOperationTypes_executesSuccessfully(OperationStatus operationStatus) {
        String result = ServiceOperationExecutor.execute(
            () -> "success",
            operationStatus,
            exceptionFactory
        );

        assertThat(result).isEqualTo("success");
    }

    @Nested
    @DisplayName("TraceId Branch Coverage Tests")
    class TraceIdBranchCoverageTests {

        @Test
        @DisplayName("Should create new trace ID when existing trace ID is blank")
        void testExecute_withBlankTraceId_createsNewTraceId() {
            MDC.put(MDC_REQUEST_ID_KEY, "   ");

            ServiceOperationExecutor.execute(
                () -> "test",
                OperationStatus.RETRIEVE,
                exceptionFactory
            );

            String traceId = MDC.get(MDC_REQUEST_ID_KEY);
            assertThat(traceId).isNotNull();
            assertThat(traceId).isNotBlank();
            assertThat(traceId).isNotEqualTo("   ");
        }

        @Test
        @DisplayName("Should create new trace ID when existing trace ID is empty string")
        void testExecute_withEmptyTraceId_createsNewTraceId() {
            MDC.put(MDC_REQUEST_ID_KEY, "");

            ServiceOperationExecutor.execute(
                () -> "test",
                OperationStatus.RETRIEVE,
                exceptionFactory
            );

            String traceId = MDC.get(MDC_REQUEST_ID_KEY);
            assertThat(traceId).isNotNull();
            assertThat(traceId).isNotBlank();
        }

    }

    @Nested
    @DisplayName("Programmer Error Branch Coverage Tests")
    class ProgrammerErrorBranchCoverageTests {

        @Test
        @DisplayName("Should bubble up NullPointerException with null message as programmer error")
        void testExecute_withNullPointerExceptionNullMessage_bubblesUp() {
            NullPointerException exception = new NullPointerException();

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isSameAs(exception);
        }

        @Test
        @DisplayName("Should bubble up UnsupportedOperationException as programmer error")
        void testExecute_withUnsupportedOperationException_bubblesUp() {
            UnsupportedOperationException exception = new UnsupportedOperationException("Unsupported");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isSameAs(exception);
        }

        @Test
        @DisplayName("Should bubble up IndexOutOfBoundsException as programmer error")
        void testExecute_withIndexOutOfBoundsException_bubblesUp() {
            IndexOutOfBoundsException exception = new IndexOutOfBoundsException("Index out of bounds");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isSameAs(exception);
        }

        @Test
        @DisplayName("Should bubble up ArrayIndexOutOfBoundsException as programmer error")
        void testExecute_withArrayIndexOutOfBoundsException_bubblesUp() {
            ArrayIndexOutOfBoundsException exception = new ArrayIndexOutOfBoundsException("Array index out of bounds");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isSameAs(exception);
        }

        @Test
        @DisplayName("Should bubble up ArithmeticException as programmer error")
        void testExecute_withArithmeticException_bubblesUp() {
            ArithmeticException exception = new ArithmeticException("Division by zero");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isSameAs(exception);
        }

        @Test
        @DisplayName("Should bubble up NumberFormatException as programmer error")
        void testExecute_withNumberFormatException_bubblesUp() {
            NumberFormatException exception = new NumberFormatException("Invalid number format");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isSameAs(exception);
        }

        @Test
        @DisplayName("Should wrap non-programmer-error RuntimeException (covers NumberFormatException false branch)")
        void testExecute_withNonProgrammerErrorRuntimeException_wrapsException() {
            RuntimeException customException = new RuntimeException("Custom non-programmer error") {
            };

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw customException;
                    },
                    OperationStatus.OTHER,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error occurred during other");
        }

        @Test
        @DisplayName("Should bubble up IllegalStateException as programmer error")
        void testExecute_withIllegalStateExceptionNotNullPointer_bubblesUp() {
            IllegalStateException exception = new IllegalStateException("Illegal state - not null pointer");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isSameAs(exception)
                .isNotInstanceOf(NullPointerException.class)
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Should wrap SecurityException as non-programmer error")
        void testExecute_withSecurityException_wrapsAsUnexpectedError() {
            SecurityException exception = new SecurityException("Security violation");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error occurred during retrieve")
                .isNotSameAs(exception);
        }

        @Test
        @DisplayName("Should bubble up ClassCastException - covering NullPointerException false branch on line 121")
        void testExecute_withClassCastException_bubblesUpCoveringLine121() {
            ClassCastException exception = new ClassCastException("Cannot cast String to Integer");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isSameAs(exception)
                .isNotInstanceOf(NullPointerException.class)
                .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("Should bubble up ArithmeticException - covering multiple false branches including line 121")
        void testExecute_withArithmeticException_coversMultipleFalseBranches() {
            ArithmeticException exception = new ArithmeticException("Division by zero");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.UPDATE,
                    exceptionFactory
                )
            ).isSameAs(exception)
                .isNotInstanceOf(NullPointerException.class)
                .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("Should bubble up StringIndexOutOfBoundsException as programmer error (subclass coverage)")
        void testExecute_withStringIndexOutOfBoundsException_bubblesUp() {
            StringIndexOutOfBoundsException exception = new StringIndexOutOfBoundsException("String index: 10");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isSameAs(exception)
                .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("Should NOT bubble up IllegalMonitorStateException - not a programmer error")
        void testExecute_withIllegalMonitorStateException_wrapsAsUnexpectedError() {
            IllegalMonitorStateException exception = new IllegalMonitorStateException("Monitor state error");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error occurred during save")
                .isNotSameAs(exception);
        }

        @Test
        @DisplayName("Should NOT bubble up NegativeArraySizeException - not a programmer error")
        void testExecute_withNegativeArraySizeException_wrapsAsUnexpectedError() {
            NegativeArraySizeException exception = new NegativeArraySizeException("Negative array size");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.DELETE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error occurred during delete")
                .isNotSameAs(exception);
        }

        @Test
        @DisplayName("isProgrammerError returns false when all instanceof checks fail - final branch coverage")
        void testExecute_withCustomRuntimeException_allInstanceofChecksFail() {
            RuntimeException customException = new RuntimeException("Not a programmer error") {
                private static final long serialVersionUID = 1L;
            };

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw customException;
                    },
                    OperationStatus.OTHER,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error occurred during other")
                .isNotSameAs(customException)
                .isNotInstanceOf(NullPointerException.class)
                .isNotInstanceOf(IllegalStateException.class)
                .isNotInstanceOf(UnsupportedOperationException.class)
                .isNotInstanceOf(ClassCastException.class)
                .isNotInstanceOf(IndexOutOfBoundsException.class)
                .isNotInstanceOf(ArithmeticException.class)
                .isNotInstanceOf(NumberFormatException.class);
        }

    }

    @Nested
    @DisplayName("Unhandled Database Exception Branch Coverage Tests")
    class UnhandledDatabaseExceptionTests {

        @Test
        @DisplayName("Should handle unrecognized DataAccessException with fallback message")
        void testExecute_withUnrecognizedDataAccessException_createsFallbackException() {
            DataAccessException exception = new DataAccessException("Custom data access error") {
            };

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.UPDATE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected database error during update");
        }

    }

    @Nested
    @DisplayName("InvalidDataAccessResourceUsage Root Cause Message Tests")
    class InvalidDataAccessResourceUsageTests {

        @Test
        @DisplayName("Should handle column does not exist error message")
        void testExecute_withColumnDoesNotExist_createsColumnNotExistMessage() {
            InvalidDataAccessResourceUsageException exception = new InvalidDataAccessResourceUsageException(
                "Error",
                new RuntimeException("ERROR: column \"user_name\" does not exist")
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database column does not exist")
                .hasMessageContaining("Please verify entity mappings match the database schema");
        }

        @Test
        @DisplayName("Should handle message with Table but not doesn't exist")
        void testExecute_withTableButNotDoesntExist_createsGenericMessage() {
            InvalidDataAccessResourceUsageException exception = new InvalidDataAccessResourceUsageException(
                "Error",
                new RuntimeException("Table 'users' is locked")
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to access database resource");
        }

        @Test
        @DisplayName("Should handle SQL syntax error message")
        void testExecute_withSyntaxError_createsSyntaxErrorMessage() {
            InvalidDataAccessResourceUsageException exception = new InvalidDataAccessResourceUsageException(
                "Error",
                new RuntimeException("Syntax error in SQL statement")
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid SQL query syntax detected");
        }

        @Test
        @DisplayName("Should handle generic resource usage error when no specific pattern matches")
        void testExecute_withGenericResourceUsageError_createsGenericMessage() {
            InvalidDataAccessResourceUsageException exception = new InvalidDataAccessResourceUsageException(
                "Error",
                new RuntimeException("Some other database resource issue")
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to access database resource")
                .hasMessageContaining("Please verify the database configuration and schema");
        }

        @Test
        @DisplayName("Should handle null root cause message")
        void testExecute_withNullRootCauseMessage_createsGenericMessage() {
            RuntimeException causeWithNullMessage = new RuntimeException((String) null);
            InvalidDataAccessResourceUsageException exception = new InvalidDataAccessResourceUsageException(
                "Error",
                causeWithNullMessage
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to access database resource");
        }

    }

    @Nested
    @DisplayName("EntityNotFoundException Switch Branch Tests")
    class EntityNotFoundSwitchBranchTests {

        @Test
        @DisplayName("Should handle EntityNotFoundException for SAVE operation (default branch)")
        void testExecute_withEntityNotFoundOnSave_usesDefaultMessage() {
            EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessage("Entity not found");
        }

        @Test
        @DisplayName("Should handle EntityNotFoundException for OTHER operation (default branch)")
        void testExecute_withEntityNotFoundOnOther_usesDefaultMessage() {
            EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.OTHER,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessage("Entity not found");
        }

    }

    @Nested
    @DisplayName("DataIntegrityViolation Switch Branch Tests")
    class DataIntegrityViolationSwitchBranchTests {

        @Test
        @DisplayName("Should handle DataIntegrityViolationException for UPDATE operation")
        void testExecute_withDataIntegrityViolationOnUpdate_createsUpdateMessage() {
            DataIntegrityViolationException exception = new DataIntegrityViolationException("Integrity violation");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.UPDATE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Data integrity violation")
                .hasMessageContaining("update violates constraints");
        }

        @Test
        @DisplayName("Should handle DataIntegrityViolationException for RETRIEVE operation (default branch)")
        void testExecute_withDataIntegrityViolationOnRetrieve_usesDefaultMessage() {
            DataIntegrityViolationException exception = new DataIntegrityViolationException("Integrity violation");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Data integrity violation")
                .hasMessageContaining("constraint violation");
        }

        @Test
        @DisplayName("Should handle DataIntegrityViolationException for OTHER operation (default branch)")
        void testExecute_withDataIntegrityViolationOnOther_usesDefaultMessage() {
            DataIntegrityViolationException exception = new DataIntegrityViolationException("Integrity violation");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.OTHER,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Data integrity violation")
                .hasMessageContaining("constraint violation");
        }

    }

    @Nested
    @DisplayName("ConstraintViolation Switch Branch Tests")
    class ConstraintViolationSwitchBranchTests {

        @Test
        @DisplayName("Should handle ConstraintViolationException for UPDATE operation")
        void testExecute_withConstraintViolationOnUpdate_createsUpdateMessage() {
            ConstraintViolationException exception = new ConstraintViolationException(
                "Constraint violation",
                new SQLException(),
                "constraint"
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.UPDATE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Constraint violation")
                .hasMessageContaining("update fails validation rules");
        }

        @Test
        @DisplayName("Should handle ConstraintViolationException for DELETE operation")
        void testExecute_withConstraintViolationOnDelete_createsDeleteMessage() {
            ConstraintViolationException exception = new ConstraintViolationException(
                "Constraint violation",
                new SQLException(),
                "constraint"
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.DELETE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Constraint violation")
                .hasMessageContaining("delete operation prevented");
        }

        @Test
        @DisplayName("Should handle ConstraintViolationException for RETRIEVE operation (default branch)")
        void testExecute_withConstraintViolationOnRetrieve_usesDefaultMessage() {
            ConstraintViolationException exception = new ConstraintViolationException(
                "Constraint violation",
                new SQLException(),
                "constraint"
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Constraint violation")
                .hasMessageContaining("validation rules violated");
        }

        @Test
        @DisplayName("Should handle ConstraintViolationException for OTHER operation (default branch)")
        void testExecute_withConstraintViolationOnOther_usesDefaultMessage() {
            ConstraintViolationException exception = new ConstraintViolationException(
                "Constraint violation",
                new SQLException(),
                "constraint"
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.OTHER,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Constraint violation")
                .hasMessageContaining("validation rules violated");
        }

    }

    @Nested
    @DisplayName("RestClient Exception Branch Coverage Tests")
    class RestClientExceptionBranchTests {

        @Test
        @DisplayName("Should handle generic RestClientException")
        void testExecute_withGenericRestClientException_createsExternalServiceException() {
            RestClientException exception = new RestClientException("Generic REST client error");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("External service error during retrieve")
                .hasMessageContaining("Generic REST client error")
                .satisfies(ex -> {
                    ExternalServiceException ese = (ExternalServiceException) ex;
                    assertThat(ese.getCustomProperties().get("httpStatusCode")).isNull();
                    assertThat(ese.getCustomProperties().get("responseBody")).isNull();
                });
        }

        @Test
        @DisplayName("Should handle RestClientException with IOException cause")
        void testExecute_withRestClientExceptionWithIOCause_createsExternalServiceException() {
            RestClientException exception = new RestClientException(
                "I/O error occurred", new IOException("Connection reset"));

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("External service error during save");
        }

    }

    @Nested
    @DisplayName("Response Body Null/Empty Check Tests")
    class ResponseBodyNullEmptyTests {

        @Test
        @DisplayName("Should handle HttpStatusCodeException with null response body gracefully")
        void testExecute_withNullResponseBody_handlesGracefully() {
            HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
            when(exception.getStatusCode()).thenReturn(HttpStatusCode.valueOf(500));
            when(exception.getResponseBodyAsString()).thenReturn(null);
            when(exception.getMessage()).thenReturn("Internal Server Error");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("External service error");
        }

        @Test
        @DisplayName("Should handle HttpStatusCodeException with empty response body string")
        void testExecute_withEmptyResponseBody_returnsEmptyString() {
            HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Server Error",
                null,
                new byte[0],
                null
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .satisfies(ex -> {
                    ExternalServiceException ese = (ExternalServiceException) ex;
                    assertThat(ese.getCustomProperties().get("responseBody")).isNull();
                });
        }

        @Test
        @DisplayName("Should handle HttpStatusCodeException with whitespace-only response body")
        void testExecute_withWhitespaceResponseBody_includesInResponse() {
            HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Server Error",
                null,
                "   ".getBytes(),
                null
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .satisfies(ex -> {
                    ExternalServiceException ese = (ExternalServiceException) ex;
                    assertThat(ese.getCustomProperties().get("responseBody")).isEqualTo("   ");
                });
        }

    }

    @Nested
    @DisplayName("HTTP Status Resolve Tests")
    class HttpStatusResolveTests {

        @Test
        @DisplayName("Should handle non-standard HTTP status code that cannot be resolved")
        void testExecute_withNonStandardHttpStatus_returnsHttpStatusNumber() {
            HttpClientErrorException exception = HttpClientErrorException.create(
                "Custom error",
                HttpStatus.valueOf(418),
                "I'm a teapot",
                null,
                "{\"error\": \"teapot\"}".getBytes(),
                null
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("I'm a teapot");
        }

        @Test
        @DisplayName("Should handle standard HTTP status codes correctly")
        void testExecute_withStandardHttpStatus_returnsReasonPhrase() {
            HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                "{\"error\": \"not found\"}".getBytes(),
                null
            );

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Not Found");
        }

        @Test
        @DisplayName("Should format HTTP status code as 'HTTP xxx' when HttpStatus.resolve returns null")
        void testExecute_withUnresolvableHttpStatus_formatsAsHttpNumber() {
            HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
            HttpStatusCode customStatusCode = HttpStatusCode.valueOf(599);
            when(exception.getStatusCode()).thenReturn(customStatusCode);
            when(exception.getResponseBodyAsString()).thenReturn("");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.RETRIEVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("HTTP 599");
        }

    }

    @Nested
    @DisplayName("PersistenceException instanceof Check Tests")
    class PersistenceExceptionInstanceofTests {

        @Test
        @DisplayName("Should handle generic PersistenceException")
        void testExecute_withGenericPersistenceException_handlesPersistenceError() {
            PersistenceException exception = new PersistenceException("Generic persistence error");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessage("Persistence error occurred");
        }

        @Test
        @DisplayName("Should handle PersistenceException subclass")
        void testExecute_withPersistenceExceptionSubclass_handlesPersistenceError() {
            PersistenceException exception = new PersistenceException(
                "Persistence layer failure", new RuntimeException("Underlying cause"));

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.UPDATE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessage("Persistence error occurred");
        }

    }

    @Nested
    @DisplayName("Response Body Return Null Statement Test")
    class ResponseBodyReturnNullTests {

        @Test
        @DisplayName("Should return null from handleDataAccessExceptions when no condition matches")
        void testHandleDataAccessExceptions_whenNoConditionMatches_returnsNull() {
            DataAccessException exception = new DataAccessException("Unhandled exception type") {
            };

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.DELETE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected database error during delete");
        }

    }

    @Nested
    @DisplayName("ResourceAccessException instanceof Check Tests")
    class ResourceAccessExceptionInstanceofTests {

        @Test
        @DisplayName("Should handle ResourceAccessException with connection timeout")
        void testExecute_withResourceAccessExceptionTimeout_createsUnavailableMessage() {
            ResourceAccessException exception = new ResourceAccessException("Connection timed out");

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("External service unavailable during save")
                .hasMessageContaining("Connection timed out");
        }

        @Test
        @DisplayName("Should handle ResourceAccessException with connection refused")
        void testExecute_withResourceAccessExceptionRefused_createsUnavailableMessage() {
            ResourceAccessException exception = new ResourceAccessException(
                "Connection refused", new IOException("Connection refused"));

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.DELETE,
                    exceptionFactory
                )
            ).isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("External service unavailable during delete");
        }

    }

    @Nested
    @DisplayName("DataAccessException Null Check Tests")
    class DataAccessExceptionNullCheckTests {

        @Test
        @DisplayName("Should fall through to unhandled exception when handleDataAccessExceptions returns null")
        void testExecute_whenDataAccessExceptionNotMatched_fallsToUnhandled() {
            DataAccessException exception = new DataAccessException("Plain data access error") {
            };

            assertThatThrownBy(() ->
                ServiceOperationExecutor.execute(
                    () -> {
                        throw exception;
                    },
                    OperationStatus.SAVE,
                    exceptionFactory
                )
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected database error during save");
        }

    }

}
