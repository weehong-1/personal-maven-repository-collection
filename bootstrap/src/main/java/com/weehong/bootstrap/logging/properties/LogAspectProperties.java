package com.weehong.bootstrap.logging.properties;

import com.weehong.bootstrap.logging.constants.AspectConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.logging.aspect")
public record LogAspectProperties(
    boolean enabled,
    long slowExecutionThresholdMs
) {
    public LogAspectProperties {
        if (slowExecutionThresholdMs <= 0) {
            slowExecutionThresholdMs = AspectConstant.SLOW_EXECUTION_THRESHOLD_MS;
        }
    }
}
