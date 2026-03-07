package com.weehong.bootstrap.core.utils;

import com.weehong.bootstrap.core.enums.OperationStatus;
import com.weehong.bootstrap.core.exceptions.types.ExceptionFactory;
import com.weehong.bootstrap.core.exceptions.types.ExternalServiceException;
import com.weehong.bootstrap.core.exceptions.types.InvalidFileException;
import com.weehong.bootstrap.core.exceptions.types.ResourceNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.function.Supplier;

import static com.weehong.bootstrap.core.constants.ServiceOperationExecutorConstant.MAX_RESPONSE_BODY_SIZE;
import static com.weehong.bootstrap.core.constants.ServiceOperationExecutorConstant.MDC_REQUEST_ID_KEY;
import static com.weehong.bootstrap.core.constants.ServiceOperationExecutorConstant.TRUNCATION_INDICATOR;
import static java.util.UUID.randomUUID;

public final class ServiceOperationExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOperationExecutor.class);

    private ServiceOperationExecutor() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static <T> T execute(Supplier<T> operation,
                                OperationStatus operationType,
                                ExceptionFactory exceptionFactory) {
        Objects.requireNonNull(operation, "Operation supplier must not be null");
        Objects.requireNonNull(operationType, "Operation type must not be null");
        Objects.requireNonNull(exceptionFactory, "ExceptionFactory must not be null");

        String traceId = getOrCreateTraceId();
        LOGGER.debug("[{}] Executing {} operation", traceId, operationType.name().toLowerCase());

        try {
            T result = operation.get();
            LOGGER.debug("[{}] {} operation completed successfully",
                traceId,
                operationType.name().toLowerCase());
            return result;
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("[{}] {} operation failed with illegal argument: {}",
                traceId, operationType, ex.getMessage());
            throw ex;
        } catch (DataAccessException | PersistenceException ex) {
            LOGGER.error("[{}] {} operation failed with database exception: {}",
                traceId, operationType, ex.getMessage(), ex);
            throw handleDatabaseException(ex, operationType, exceptionFactory, traceId);
        } catch (RestClientException ex) {
            LOGGER.error("[{}] {} operation failed with RestClient exception: {}",
                traceId, operationType, ex.getMessage());
            throw handleRestClientException(ex, operationType, traceId);
        } catch (InvalidFileException ex) {
            LOGGER.error("[{}] {} operation failed with invalid file: {}",
                traceId, operationType, ex.getMessage());
            throw exceptionFactory.create(ex.getMessage(), ex);
        } catch (ResourceNotFoundException ex) {
            LOGGER.warn("[{}] {} operation failed - resource not found: {}",
                traceId, operationType, ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            if (isProgrammerError(ex)) {
                LOGGER.error("[{}] {} operation failed with programmer error - bubbling up: {}",
                    traceId, operationType, ex.getClass().getSimpleName(), ex);
                throw ex;
            }

            LOGGER.error("[{}] {} operation failed with unexpected exception: {}",
                traceId, operationType, ex.getMessage(), ex);

            throw exceptionFactory.create(
                "Unexpected error occurred during " + operationType.name().toLowerCase(),
                ex
            );
        }
    }

    public static void executeVoid(Runnable operation,
                                   OperationStatus operationType,
                                   ExceptionFactory exceptionFactory) {
        execute(() -> {
            operation.run();
            return null;
        }, operationType, exceptionFactory);
    }

    private static String getOrCreateTraceId() {
        String existingTraceId = MDC.get(MDC_REQUEST_ID_KEY);

        if (existingTraceId != null
            && !existingTraceId.isBlank()) {
            return existingTraceId;
        }

        String newTraceId = randomUUID().toString();
        MDC.put(MDC_REQUEST_ID_KEY, newTraceId);
        return newTraceId;
    }

    private static boolean isProgrammerError(RuntimeException ex) {
        return ex instanceof NullPointerException
            || ex instanceof IllegalStateException
            || ex instanceof UnsupportedOperationException
            || ex instanceof ClassCastException
            || ex instanceof IndexOutOfBoundsException
            || ex instanceof ArithmeticException
            || ex instanceof NumberFormatException;
    }

    private static RuntimeException handleDatabaseException(
        Exception ex,
        OperationStatus operationType,
        ExceptionFactory exceptionFactory,
        String traceId
    ) {
        LOGGER.debug("[{}] Handling database exception: {}", traceId, ex.getClass().getSimpleName());

        RuntimeException lockingException = handleLockingExceptions(ex, exceptionFactory);

        if (lockingException != null) {
            return lockingException;
        }

        RuntimeException entityException = handleEntityExceptions(ex, operationType, exceptionFactory);

        if (entityException != null) {
            return entityException;
        }

        RuntimeException dataAccessException = handleDataAccessExceptions(ex, exceptionFactory);

        if (dataAccessException != null) {
            return dataAccessException;
        }

        LOGGER.warn("[{}] Unhandled database exception type: {}", traceId, ex.getClass().getName());
        return exceptionFactory.create(
            "Unexpected database error during " + operationType.name().toLowerCase(),
            ex
        );
    }

    private static RuntimeException handleLockingExceptions(
        Exception ex,
        ExceptionFactory exceptionFactory
    ) {
        if (ex instanceof OptimisticLockingFailureException) {
            return exceptionFactory.create(
                "Concurrent modification detected - please retry the operation",
                ex
            );
        }

        if (ex instanceof DeadlockLoserDataAccessException) {
            return exceptionFactory.create(
                "Database deadlock detected - please retry the operation",
                ex
            );
        }

        if (ex instanceof CannotAcquireLockException) {
            return exceptionFactory.create(
                "Cannot acquire database lock - resource may be in use",
                ex
            );
        }

        if (ex instanceof PessimisticLockingFailureException) {
            return exceptionFactory.create(
                "Resource is locked by another transaction",
                ex
            );
        }

        return null;
    }

    private static RuntimeException handleEntityExceptions(
        Exception ex,
        OperationStatus operationType,
        ExceptionFactory exceptionFactory
    ) {
        if (ex instanceof EntityNotFoundException) {
            return handleEntityNotFound(operationType, ex, exceptionFactory);
        }

        if (ex instanceof EntityExistsException) {
            return exceptionFactory.create("Entity already exists", ex);
        }

        if (ex instanceof DataIntegrityViolationException) {
            return handleDataIntegrityViolation(operationType, ex, exceptionFactory);
        }

        if (ex instanceof ConstraintViolationException) {
            return handleConstraintViolation(operationType, ex, exceptionFactory);
        }

        return null;
    }

    private static RuntimeException handleDataAccessExceptions(
        Exception ex,
        ExceptionFactory exceptionFactory
    ) {
        if (ex instanceof InvalidDataAccessResourceUsageException idaEx) {
            return handleInvalidDataAccessResourceUsage(idaEx, exceptionFactory);
        }

        if (ex instanceof JpaSystemException) {
            return exceptionFactory.create("JPA system error occurred", ex);
        }

        if (ex instanceof InvalidDataAccessApiUsageException) {
            return exceptionFactory.create("Invalid usage of the Data Access API", ex);
        }

        if (ex instanceof PersistenceException) {
            return exceptionFactory.create("Persistence error occurred", ex);
        }

        return null;
    }

    private static RuntimeException handleInvalidDataAccessResourceUsage(
        InvalidDataAccessResourceUsageException ex,
        ExceptionFactory exceptionFactory
    ) {
        String rootCauseMessage = ex.getMostSpecificCause().getMessage();
        String detailMessage;

        if (rootCauseMessage != null) {
            if (rootCauseMessage.contains("Table") && rootCauseMessage.contains("doesn't exist")) {
                detailMessage = "Database table does not exist. "
                    + "Please ensure the database schema is up to date";
            } else if (rootCauseMessage.contains("column") && rootCauseMessage.contains("does not exist")) {
                detailMessage = "Database column does not exist. "
                    + "Please verify entity mappings match the database schema";
            } else if (rootCauseMessage.contains("Unknown column")) {
                detailMessage = "Database column mismatch detected. "
                    + "Please verify entity mappings match the database schema";
            } else if (rootCauseMessage.contains("Syntax error")) {
                detailMessage = "Invalid SQL query syntax detected";
            } else {
                detailMessage = "Unable to access database resource. "
                    + "Please verify the database configuration and schema";
            }
        } else {
            detailMessage = "Unable to access database resource. "
                + "Please verify the database configuration and schema";
        }

        return exceptionFactory.create(detailMessage, ex);
    }

    private static RuntimeException handleEntityNotFound(
        OperationStatus type,
        Exception ex,
        ExceptionFactory factory
    ) {
        return switch (type) {
            case RETRIEVE -> factory.create("Entity not found", ex);
            case UPDATE -> factory.create(
                "Entity does not exist or was deleted - cannot update",
                ex
            );
            case DELETE -> factory.create(
                "Entity does not exist or was already deleted",
                ex
            );
            default -> factory.create("Entity not found", ex);
        };
    }

    private static RuntimeException handleDataIntegrityViolation(
        OperationStatus type,
        Exception ex,
        ExceptionFactory factory
    ) {
        String baseMessage = switch (type) {
            case SAVE -> "duplicate entry or constraint failure";
            case UPDATE -> "update violates constraints";
            case DELETE -> "cannot delete due to foreign key constraint";
            default -> "constraint violation";
        };

        return factory.create("Data integrity violation - " + baseMessage, ex);
    }

    private static RuntimeException handleConstraintViolation(
        OperationStatus type,
        Exception ex,
        ExceptionFactory factory
    ) {
        String baseMessage = switch (type) {
            case SAVE -> "entity fails validation rules";
            case UPDATE -> "update fails validation rules";
            case DELETE -> "delete operation prevented";
            default -> "validation rules violated";
        };

        return factory.create("Constraint violation - " + baseMessage, ex);
    }

    private static RuntimeException handleRestClientException(
        RestClientException ex,
        OperationStatus type,
        String traceId
    ) {
        if (ex instanceof HttpStatusCodeException statusCodeEx) {
            int status = statusCodeEx.getStatusCode().value();
            String response = extractRestClientResponseBody(statusCodeEx, traceId);

            String message = String.format(
                "External service error during %s: %s",
                type.name().toLowerCase(),
                resolveHttpMessage(status)
            );

            return new ExternalServiceException(message, ex, status, response);
        }

        if (ex instanceof ResourceAccessException) {
            String message = String.format(
                "External service unavailable during %s: %s",
                type.name().toLowerCase(),
                ex.getMessage()
            );
            return new ExternalServiceException(message, ex, null, null);
        }

        String message = String.format(
            "External service error during %s: %s",
            type.name().toLowerCase(),
            ex.getMessage()
        );
        return new ExternalServiceException(message, ex, null, null);
    }

    private static String extractRestClientResponseBody(
        HttpStatusCodeException ex,
        String traceId
    ) {
        String responseBody = ex.getResponseBodyAsString();

        if (responseBody == null || responseBody.isEmpty()) {
            return "";
        }

        return truncateResponseBody(responseBody, traceId);
    }

    private static String truncateResponseBody(String responseBody, String traceId) {
        if (responseBody.length() > MAX_RESPONSE_BODY_SIZE) {
            String truncated = responseBody.substring(0, MAX_RESPONSE_BODY_SIZE) + TRUNCATION_INDICATOR;
            LOGGER.debug("[{}] RestClient response body truncated from {} to {} bytes",
                traceId, responseBody.length(), MAX_RESPONSE_BODY_SIZE);
            return truncated;
        }

        return responseBody;
    }

    private static String resolveHttpMessage(int status) {
        HttpStatus httpStatus = HttpStatus.resolve(status);
        return httpStatus != null
            ? httpStatus.getReasonPhrase()
            : "HTTP " + status;
    }

}
