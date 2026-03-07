package com.weehong.bootstrap.tracing.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.tracing")
public record TracingProperties(
    boolean enabled,
    double samplingProbability,
    String otlpEndpoint
) {
    private static final double DEFAULT_SAMPLING_PROBABILITY = 1.0;
    private static final String DEFAULT_OTLP_ENDPOINT = "http://localhost:4317";

    public TracingProperties {

        if (samplingProbability <= 0 || samplingProbability > 1.0) {
            samplingProbability = DEFAULT_SAMPLING_PROBABILITY;
        }

        if (otlpEndpoint == null || otlpEndpoint.isBlank()) {
            otlpEndpoint = DEFAULT_OTLP_ENDPOINT;
        }
    }
}
