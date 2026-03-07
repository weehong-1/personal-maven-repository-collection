package com.weehong.bootstrap.logging.aspects;

import com.weehong.bootstrap.logging.exceptions.MethodExecutionException;
import com.weehong.bootstrap.logging.properties.LogAspectProperties;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

import static com.weehong.bootstrap.logging.constants.AspectConstant.MDC_ENDPOINT;
import static com.weehong.bootstrap.logging.constants.AspectConstant.MDC_HTTP_METHOD;
import static com.weehong.bootstrap.logging.constants.AspectConstant.MDC_METHOD;
import static com.weehong.bootstrap.logging.constants.AspectConstant.MDC_REQUEST_ID;
import static com.weehong.bootstrap.logging.constants.AspectConstant.MDC_USER_ID;
import static com.weehong.bootstrap.logging.constants.AspectConstant.STATUS_TAG;
import static java.util.UUID.randomUUID;

@Aspect
@Component
@ConditionalOnProperty(prefix = "app.logging.aspect",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);
    private static final int REQUEST_ID_LENGTH = 8;

    private final MeterRegistry meterRegistry;
    private final LogAspectProperties logAspectProperties;

    public LogAspect(MeterRegistry meterRegistry, LogAspectProperties logAspectProperties) {
        this.meterRegistry = meterRegistry;
        this.logAspectProperties = logAspectProperties;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || "
        + "within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {

    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {

    }

    @Around("controllerMethods() || serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();

        boolean isNewRequestId = MDC.get(MDC_REQUEST_ID) == null;

        if (isNewRequestId) {
            MDC.put(MDC_REQUEST_ID, initRequestId());
        }

        MDC.put(MDC_METHOD, method);

        String userId = extractUserId();
        boolean hasUserId = userId != null;

        if (hasUserId) {
            MDC.put(MDC_USER_ID, userId);
        }

        HttpServletRequest request = currentHttpRequest();
        boolean hasHttpContext = false;

        if (request != null) {
            MDC.put(MDC_HTTP_METHOD, request.getMethod());
            MDC.put(MDC_ENDPOINT, request.getRequestURI());
            hasHttpContext = true;
        }

        try {
            Object result = joinPoint.proceed();
            Long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            recordMetrics(method, durationMs, false);
            logSuccess(method, durationMs);

            return result;
        } catch (Throwable t) {
            Long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            recordMetrics(method, durationMs, true);
            logFailure(method, durationMs, t);

            if (t instanceof Error) {
                throw t;
            }

            if (t instanceof RuntimeException rtEx) {
                throw rtEx;
            }

            throw new MethodExecutionException(method, durationMs, t);
        } finally {
            if (isNewRequestId) {
                MDC.remove(MDC_REQUEST_ID);
            }

            MDC.remove(MDC_METHOD);

            if (hasUserId) {
                MDC.remove(MDC_USER_ID);
            }

            if (hasHttpContext) {
                MDC.remove(MDC_HTTP_METHOD);
                MDC.remove(MDC_ENDPOINT);
            }
        }
    }

    private String initRequestId() {
        return randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, REQUEST_ID_LENGTH)
            .toUpperCase();
    }

    @SuppressWarnings("java:S1181")
    private String extractUserId() {
        try {
            return SecurityUserExtractor.extract();
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    private HttpServletRequest currentHttpRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                return servletRequestAttributes.getRequest();
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void recordMetrics(String method, Long durationMs, boolean failed) {
        String status = failed
            ? "failure"
            : "success";

        meterRegistry
            .timer("app.method.execution", "method", method, STATUS_TAG, status)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }

    private void logSuccess(String method, Long durationMs) {
        if (durationMs >= logAspectProperties.slowExecutionThresholdMs()) {
            LOGGER.warn("Method {} executed slowly in {} ms", method, durationMs);
        } else {
            LOGGER.info("Method {} executed successfully in {} ms", method, durationMs);
        }
    }

    private void logFailure(String method, Long durationMs, Throwable t) {
        LOGGER.error(
            "Method {} failed in {} ms with {}: {}",
            method,
            durationMs,
            t.getClass().getSimpleName(),
            t.getMessage());
    }

    /**
     * Isolated inner class that references Spring Security classes.
     * This class is only loaded when {@link #extractUserId()} is called,
     * and if Spring Security is not on the classpath, a {@link NoClassDefFoundError}
     * is caught gracefully by the caller.
     */
    private static final class SecurityUserExtractor {

        private SecurityUserExtractor() {

        }

        static String extract() {
            try {
                Object authentication = org.springframework.security.core.context
                    .SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof org.springframework.security.core.Authentication auth
                    && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                    return auth.getName();
                }

                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
