package com.weehong.bootstrap.logging.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.logging.loki")
public record LokiProperties(
    boolean enabled,
    String url
) {
    private static final String DEFAULT_URL = "http://localhost:3100/loki/api/v1/push";

    public LokiProperties {
        if (url == null || url.isBlank()) {
            url = DEFAULT_URL;
        }
    }
}
