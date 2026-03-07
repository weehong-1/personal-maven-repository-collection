package com.weehong.bootstrap.metrics.configurations;

import com.weehong.bootstrap.logging.properties.LogAspectProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MetricsConfigurationTest {

    private static final long DEFAULT_SLOW_THRESHOLD_MS = 1000L;

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public LogAspectProperties logAspectProperties() {
        return new LogAspectProperties(true, DEFAULT_SLOW_THRESHOLD_MS);
    }
}
