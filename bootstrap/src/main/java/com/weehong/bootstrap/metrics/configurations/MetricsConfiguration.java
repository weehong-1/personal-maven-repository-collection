package com.weehong.bootstrap.metrics.configurations;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MetricsConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment environment) {
        return registry -> registry.config()
            .commonTags("application", environment.getProperty("spring.application.name", "upmatches"));
    }

    @Bean
    public LogbackMetrics logbackMetrics() {
        return new LogbackMetrics();
    }
}
