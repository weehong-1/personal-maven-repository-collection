package com.weehong.bootstrap.database.configurations;

import com.weehong.bootstrap.database.enums.DatabaseType;
import com.weehong.bootstrap.database.properties.DatabaseProperty;
import com.weehong.bootstrap.database.properties.HikariProperty;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;

import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_APPLICATION_NAME;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_CONNECTION_TIMEOUT;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_IDLE_TIMEOUT;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_INITIALIZATION_FAIL_TIMEOUT;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_MAXIMUM_POOL_SIZE;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_MAX_LIFETIME;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_MINIMUM_IDLE;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_POOL_NAME;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_H2;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_MYSQL;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_POSTGRESQL;

@Configuration(proxyBeanMethods = false)
public final class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final DatabaseProperty databaseProperty;
    private final Environment environment;
    private final MeterRegistry meterRegistry;

    @Autowired
    public DatabaseConfiguration(DatabaseProperty databaseProperty,
                                 Environment environment,
                                 @Nullable MeterRegistry meterRegistry) {
        this.databaseProperty = databaseProperty;
        this.environment = environment;
        this.meterRegistry = meterRegistry;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(
        prefix = "app.database",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public DataSource dataSource() {
        String jdbcUrl = databaseProperty.getUrl();

        if (!StringUtils.hasText(jdbcUrl)) {
            throw new IllegalStateException("Database URL must be configured");
        }

        String resolvedDriverClassName = resolveDriverClassName(jdbcUrl);
        String databaseTypeName = getDatabaseType(jdbcUrl);

        LOGGER.info("Initializing HikariCP DataSource for {}", databaseTypeName);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(databaseProperty.getUsername());
        hikariConfig.setPassword(databaseProperty.getPassword());
        hikariConfig.setDriverClassName(resolvedDriverClassName);
        hikariConfig.setPoolName(resolvePoolName());

        hikariConfig.setAutoCommit(false);
        hikariConfig.setReadOnly(false);

        applyPoolSizing(hikariConfig);
        applyTimeouts(hikariConfig);

        String applicationName = resolveApplicationName();
        DatabaseType databaseType = DatabaseType.fromJdbcUrl(jdbcUrl);

        if (databaseType == DatabaseType.POSTGRESQL) {
            applyPostgreSQLOptimizations(hikariConfig, applicationName);
        } else if (databaseType == DatabaseType.H2) {
            applyH2Optimizations(hikariConfig);
        }

        applyMonitoring(hikariConfig);

        logConfigurationSummary(hikariConfig, resolvedDriverClassName, applicationName);

        return new HikariDataSource(hikariConfig);
    }

    private String resolveDriverClassName(String jdbcUrl) {
        String configuredDriver = databaseProperty.getDriverClassName();

        if (StringUtils.hasText(configuredDriver)) {
            return configuredDriver;
        }

        return getDriverClassName(jdbcUrl);
    }

    private String getDriverClassName(String jdbcUrl) {
        DatabaseType databaseType = DatabaseType.fromJdbcUrl(jdbcUrl);

        if (databaseType == null) {
            LOGGER.error("Unsupported JDBC URL: {}. Supported prefixes are: {}, {}, {}",
                maskSensitiveUrl(jdbcUrl), JDBC_H2, JDBC_POSTGRESQL, JDBC_MYSQL);
            throw new IllegalArgumentException(
                String.format("Unsupported JDBC URL. Supported prefixes are: %s, %s, %s",
                    JDBC_H2, JDBC_POSTGRESQL, JDBC_MYSQL));
        }

        return databaseType.getDriverClassName();
    }

    private String getDatabaseType(String jdbcUrl) {
        DatabaseType databaseType = DatabaseType.fromJdbcUrl(jdbcUrl);
        return databaseType != null
            ? databaseType.name()
            : "Unknown";
    }

    private String resolvePoolName() {
        String[] profiles = environment.getActiveProfiles();

        return profiles.length > 0
            ? DEFAULT_POOL_NAME + "-" + String.join("-", profiles)
            : DEFAULT_POOL_NAME;
    }

    private void applyPoolSizing(HikariConfig config) {
        config.setMaximumPoolSize(getMaximumPoolSize());
        config.setMinimumIdle(getMinimumIdle());
    }

    private int getMaximumPoolSize() {
        HikariProperty hikari = databaseProperty.getHikari();

        if (hikari == null || hikari.getMaximumPoolSize() == null) {
            return DEFAULT_MAXIMUM_POOL_SIZE;
        }

        return hikari.getMaximumPoolSize();
    }

    private int getMinimumIdle() {
        HikariProperty hikari = databaseProperty.getHikari();

        if (hikari == null || hikari.getMinimumIdle() == null) {
            return DEFAULT_MINIMUM_IDLE;
        }

        return hikari.getMinimumIdle();
    }

    private void applyTimeouts(HikariConfig hikariConfig) {
        hikariConfig.setConnectionTimeout(getConnectionTimeout());
        hikariConfig.setIdleTimeout(getIdleTimeout());
        hikariConfig.setMaxLifetime(getMaxLifetime());

        HikariProperty hikari = databaseProperty.getHikari();

        if (hikari != null && hikari.getLeakDetectionThreshold() != null
            && hikari.getLeakDetectionThreshold() > 0) {
            hikariConfig.setLeakDetectionThreshold(
                hikari.getLeakDetectionThreshold()
            );
        }

        hikariConfig.setInitializationFailTimeout(getInitializationFailTimeout());
    }

    private long getInitializationFailTimeout() {
        HikariProperty hikari = databaseProperty.getHikari();

        if (hikari == null || hikari.getInitializationFailTimeout() == null) {
            return DEFAULT_INITIALIZATION_FAIL_TIMEOUT;
        }

        return hikari.getInitializationFailTimeout();
    }

    private long getConnectionTimeout() {
        HikariProperty hikari = databaseProperty.getHikari();

        if (hikari == null || hikari.getConnectionTimeout() == null) {
            return DEFAULT_CONNECTION_TIMEOUT;
        }

        return hikari.getConnectionTimeout();
    }

    private long getIdleTimeout() {
        HikariProperty hikari = databaseProperty.getHikari();

        if (hikari == null || hikari.getIdleTimeout() == null) {
            return DEFAULT_IDLE_TIMEOUT;
        }

        return hikari.getIdleTimeout();
    }

    private long getMaxLifetime() {
        HikariProperty hikari = databaseProperty.getHikari();

        if (hikari == null || hikari.getMaxLifetime() == null) {
            return DEFAULT_MAX_LIFETIME;
        }

        return hikari.getMaxLifetime();
    }

    private void applyPostgreSQLOptimizations(HikariConfig hikariConfig, String applicationName) {
        hikariConfig.addDataSourceProperty("tcpKeepAlive", "true");
        hikariConfig.addDataSourceProperty("ApplicationName", applicationName);
        hikariConfig.addDataSourceProperty("assumeMinServerVersion", "12.0");
        hikariConfig.addDataSourceProperty("reWriteBatchedInserts", "true");

        hikariConfig.addDataSourceProperty("prepareThreshold", "5");
        hikariConfig.addDataSourceProperty("preparedStatementCacheQueries", "250");
        hikariConfig.addDataSourceProperty("preparedStatementCacheSizeMiB", "5");

        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
    }

    private String resolveApplicationName() {
        String springAppName = environment.getProperty("spring.application.name");

        if (StringUtils.hasText(springAppName)) {
            String[] profiles = environment.getActiveProfiles();

            return profiles.length > 0
                ? springAppName + "-" + String.join("-", profiles)
                : springAppName;
        }

        return DEFAULT_APPLICATION_NAME;
    }

    private void applyH2Optimizations(HikariConfig hikariConfig) {
        hikariConfig.addDataSourceProperty("DB_CLOSE_DELAY", "-1");
        hikariConfig.addDataSourceProperty("DB_CLOSE_ON_EXIT", "FALSE");

        hikariConfig.addDataSourceProperty("CACHE_SIZE", "65536");
        hikariConfig.addDataSourceProperty("LOCK_TIMEOUT", "10000");

        LOGGER.debug("Applied H2-specific optimizations");
    }

    private void applyMonitoring(HikariConfig config) {
        config.setRegisterMbeans(false);

        if (meterRegistry != null) {
            config.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(meterRegistry));
            LOGGER.info("Metrics enabled with {}", meterRegistry.getClass().getSimpleName());
        }
    }

    private void logConfigurationSummary(HikariConfig config, String driver, String applicationName) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("HikariCP configured:");
            LOGGER.info("   Pool Name: {}", config.getPoolName());
            LOGGER.info("   Pool Size: min={}, max={}", config.getMinimumIdle(),
                config.getMaximumPoolSize());
            LOGGER.info("   Timeouts: connection={}ms, idle={}ms, maxLifetime={}ms",
                config.getConnectionTimeout(), config.getIdleTimeout(),
                config.getMaxLifetime());
            LOGGER.info("   Leak Detection: {}",
                config.getLeakDetectionThreshold() > 0
                    ? config.getLeakDetectionThreshold() + "ms"
                    : "disabled");
            LOGGER.info("   Database URL: {}",
                maskSensitiveUrl(databaseProperty.getUrl()));
            LOGGER.info("   Driver: {}", driver);
            LOGGER.info("   Application Name: {}", applicationName);
        }
    }

    private String maskSensitiveUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return "N/A";
        }

        return url.replaceAll("://[^@/]*@", "://***:***@")
            .replaceAll("([?&]password=)[^&]*", "$1***");
    }

}
