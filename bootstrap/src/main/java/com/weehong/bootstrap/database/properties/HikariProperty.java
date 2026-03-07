package com.weehong.bootstrap.database.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HikariProperty {

    private Integer maximumPoolSize;
    private Integer minimumIdle;
    private Long idleTimeout;
    private Long maxLifetime;
    private Long connectionTimeout;
    private Long leakDetectionThreshold;
    private Long initializationFailTimeout;

}
