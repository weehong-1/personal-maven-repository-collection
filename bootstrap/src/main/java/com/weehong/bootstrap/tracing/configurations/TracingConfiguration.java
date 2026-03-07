package com.weehong.bootstrap.tracing.configurations;

import com.weehong.bootstrap.tracing.properties.TracingProperties;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.tracing", name = "enabled", havingValue = "true")
public class TracingConfiguration {

    private final TracingProperties tracingProperties;

    public TracingConfiguration(TracingProperties tracingProperties) {
        this.tracingProperties = tracingProperties;
    }

    @Bean
    public OtlpGrpcSpanExporter otlpGrpcSpanExporter() {
        return OtlpGrpcSpanExporter.builder()
            .setEndpoint(tracingProperties.otlpEndpoint())
            .build();
    }
}
