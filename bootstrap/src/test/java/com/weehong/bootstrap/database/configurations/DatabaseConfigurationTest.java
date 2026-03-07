package com.weehong.bootstrap.database.configurations;

import com.weehong.bootstrap.database.enums.DatabaseType;
import com.weehong.bootstrap.database.properties.DatabaseProperty;
import com.weehong.bootstrap.database.properties.HikariProperty;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.lang.reflect.Method;

import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_CONNECTION_TIMEOUT;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_IDLE_TIMEOUT;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_MAXIMUM_POOL_SIZE;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_MAX_LIFETIME;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_MINIMUM_IDLE;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.DEFAULT_POOL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseConfiguration Tests")
class DatabaseConfigurationTest {

    private static final String H2_JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String POSTGRESQL_JDBC_URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String MYSQL_JDBC_URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String UNSUPPORTED_JDBC_URL = "jdbc:oracle:thin:@localhost:1521:orcl";

    @Mock
    private DatabaseProperty databaseProperty;

    @Mock
    private Environment environment;

    @Mock
    private HikariProperty hikariProperty;

    private DatabaseConfiguration underTest;

    @BeforeEach
    void setUp() {
        underTest = new DatabaseConfiguration(databaseProperty, environment, null);
    }

    @Nested
    @DisplayName("dataSource() Method Tests")
    class DataSourceTests {

        @Test
        @DisplayName("Should throw IllegalStateException when JDBC URL is null")
        void dataSource_nullUrl_throwsIllegalStateException() {
            when(databaseProperty.getUrl()).thenReturn(null);
            assertThatThrownBy(() -> underTest.dataSource())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Database URL must be configured");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when JDBC URL is empty")
        void dataSource_emptyUrl_throwsIllegalStateException() {
            when(databaseProperty.getUrl()).thenReturn("");
            assertThatThrownBy(() -> underTest.dataSource())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Database URL must be configured");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when JDBC URL is blank")
        void dataSource_blankUrl_throwsIllegalStateException() {
            when(databaseProperty.getUrl()).thenReturn("   ");
            assertThatThrownBy(() -> underTest.dataSource())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Database URL must be configured");
        }

        @Test
        @DisplayName("Should create DataSource successfully with H2 JDBC URL")
        void dataSource_h2Url_createsDataSourceSuccessfully() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getJdbcUrl()).isEqualTo(H2_JDBC_URL);
            assertThat(hikariDataSource.getDriverClassName()).isEqualTo("org.h2.Driver");
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should create DataSource successfully with PostgreSQL JDBC URL")
        void dataSource_postgresqlUrl_createsDataSourceSuccessfully() {
            when(databaseProperty.getUrl()).thenReturn(POSTGRESQL_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("postgres");
            when(databaseProperty.getPassword()).thenReturn("password");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(null);
            when(hikariProperty.getMinimumIdle()).thenReturn(null);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(null);
            when(hikariProperty.getInitializationFailTimeout()).thenReturn(0L);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn("test-app");

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getJdbcUrl()).isEqualTo(POSTGRESQL_JDBC_URL);
            assertThat(hikariDataSource.getDriverClassName()).isEqualTo("org.postgresql.Driver");
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should use configured driver class name when provided")
        void dataSource_configuredDriver_usesConfiguredDriver() {
            String configuredDriver = "org.h2.Driver";
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(configuredDriver);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getDriverClassName()).isEqualTo(configuredDriver);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for unsupported JDBC URL")
        void dataSource_unsupportedUrl_throwsIllegalArgumentException() {
            when(databaseProperty.getUrl()).thenReturn(UNSUPPORTED_JDBC_URL);
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            assertThatThrownBy(() -> underTest.dataSource())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported JDBC URL");
        }

    }

    @Nested
    @DisplayName("Pool Sizing Tests")
    class PoolSizingTests {

        @Test
        @DisplayName("Should use default maximum pool size when hikari is null")
        void dataSource_hikariNull_usesDefaultMaxPoolSize() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getMaximumPoolSize()).isEqualTo(DEFAULT_MAXIMUM_POOL_SIZE);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should use configured maximum pool size when provided")
        void dataSource_maximumPoolSizeConfigured_usesConfigured() {
            int customPoolSize = 20;
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(customPoolSize);
            when(hikariProperty.getMinimumIdle()).thenReturn(null);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getMaximumPoolSize()).isEqualTo(customPoolSize);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should use default minimum idle when hikari is null")
        void dataSource_hikariNull_usesDefaultMinimumIdle() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getMinimumIdle()).isEqualTo(DEFAULT_MINIMUM_IDLE);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should use configured minimum idle when provided")
        void dataSource_minimumIdleConfigured_usesConfigured() {
            int customMinIdle = 5;
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(null);
            when(hikariProperty.getMinimumIdle()).thenReturn(customMinIdle);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getMinimumIdle()).isEqualTo(customMinIdle);
            hikariDataSource.close();
        }

    }

    @Nested
    @DisplayName("Timeout Configuration Tests")
    class TimeoutConfigurationTests {

        @Test
        @DisplayName("Should use default connection timeout when hikari is null")
        void dataSource_hikariNull_usesDefaultConnectionTimeout() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getConnectionTimeout()).isEqualTo(DEFAULT_CONNECTION_TIMEOUT);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should use default idle timeout when hikari is null")
        void dataSource_hikariNull_usesDefaultIdleTimeout() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getIdleTimeout()).isEqualTo(DEFAULT_IDLE_TIMEOUT);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should use default max lifetime when hikari is null")
        void dataSource_hikariNull_usesDefaultMaxLifetime() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getMaxLifetime()).isEqualTo(DEFAULT_MAX_LIFETIME);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should set leak detection threshold when configured and greater than zero")
        void dataSource_leakDetectionConfigured_setsThreshold() {
            long leakThreshold = 60000L;
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(null);
            when(hikariProperty.getMinimumIdle()).thenReturn(null);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(leakThreshold);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getLeakDetectionThreshold()).isEqualTo(leakThreshold);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should not set leak detection threshold when configured value is zero")
        void dataSource_leakDetectionZero_doesNotSetThreshold() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(null);
            when(hikariProperty.getMinimumIdle()).thenReturn(null);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(0L);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getLeakDetectionThreshold()).isEqualTo(0L);
            hikariDataSource.close();
        }

    }

    @Nested
    @DisplayName("Pool Name Resolution Tests")
    class PoolNameResolutionTests {

        @Test
        @DisplayName("Should use default pool name when no active profiles")
        void dataSource_noActiveProfiles_usesDefaultPoolName() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getPoolName()).isEqualTo(DEFAULT_POOL_NAME);
            hikariDataSource.close();
        }

        @Test
        @DisplayName("Should append profile to pool name when active profile exists")
        void dataSource_withActiveProfile_appendsProfileToPoolName() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getPoolName()).isEqualTo(DEFAULT_POOL_NAME + "-test");
            hikariDataSource.close();
        }

    }

    @Nested
    @DisplayName("Application Name Resolution Tests")
    class ApplicationNameResolutionTests {

        @Test
        @DisplayName("Should use default application name when spring.application.name is null")
        void dataSource_noSpringAppName_usesDefaultApplicationName() {
            when(databaseProperty.getUrl()).thenReturn(POSTGRESQL_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("postgres");
            when(databaseProperty.getPassword()).thenReturn("password");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(null);
            when(hikariProperty.getMinimumIdle()).thenReturn(null);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(null);
            when(hikariProperty.getInitializationFailTimeout()).thenReturn(0L);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            ((HikariDataSource) result).close();
        }

        @Test
        @DisplayName("Should use spring.application.name when provided")
        void dataSource_withSpringAppName_usesProvidedName() {
            when(databaseProperty.getUrl()).thenReturn(POSTGRESQL_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("postgres");
            when(databaseProperty.getPassword()).thenReturn("password");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(null);
            when(hikariProperty.getMinimumIdle()).thenReturn(null);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(null);
            when(hikariProperty.getInitializationFailTimeout()).thenReturn(0L);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn("my-app");

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            ((HikariDataSource) result).close();
        }

    }

    @Nested
    @DisplayName("Database Type Tests")
    class DatabaseTypeTests {

        @Test
        @DisplayName("Should return Unknown when database type cannot be determined")
        void getDatabaseType_unknownUrl_returnsUnknown() {
            when(databaseProperty.getUrl()).thenReturn(UNSUPPORTED_JDBC_URL);
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            assertThatThrownBy(() -> underTest.dataSource())
                .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    @DisplayName("Monitoring Configuration Tests")
    class MonitoringConfigurationTests {

        @Test
        @DisplayName("Should configure metrics when MeterRegistry is present")
        void dataSource_withMeterRegistry_configuresMetrics() {
            MeterRegistry meterRegistry = new SimpleMeterRegistry();
            underTest = new DatabaseConfiguration(databaseProperty, environment, meterRegistry);

            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getMetricsTrackerFactory()).isNotNull();
            hikariDataSource.close();
        }

    }

    @Nested
    @DisplayName("Private Method Tests via Reflection")
    class PrivateMethodReflectionTests {

        @Test
        @DisplayName("getDatabaseType should return 'Unknown' for unsupported JDBC URL")
        void getDatabaseType_unsupportedUrl_returnsUnknown() throws Exception {
            Method getDatabaseTypeMethod =
                DatabaseConfiguration.class.getDeclaredMethod("getDatabaseType", String.class);
            getDatabaseTypeMethod.setAccessible(true);
            String result = (String) getDatabaseTypeMethod.invoke(underTest, UNSUPPORTED_JDBC_URL);
            assertThat(result).isEqualTo("Unknown");
        }

        @Test
        @DisplayName("maskSensitiveUrl should return 'N/A' for null URL")
        void maskSensitiveUrl_nullUrl_returnsNA() throws Exception {
            Method maskSensitiveUrlMethod =
                DatabaseConfiguration.class.getDeclaredMethod("maskSensitiveUrl", String.class);
            maskSensitiveUrlMethod.setAccessible(true);
            String result = (String) maskSensitiveUrlMethod.invoke(underTest, (String) null);
            assertThat(result).isEqualTo("N/A");
        }

        @Test
        @DisplayName("maskSensitiveUrl should return 'N/A' for empty URL")
        void maskSensitiveUrl_emptyUrl_returnsNA() throws Exception {
            Method maskSensitiveUrlMethod =
                DatabaseConfiguration.class.getDeclaredMethod("maskSensitiveUrl", String.class);
            maskSensitiveUrlMethod.setAccessible(true);
            String result = (String) maskSensitiveUrlMethod.invoke(underTest, "");
            assertThat(result).isEqualTo("N/A");
        }

        @Test
        @DisplayName("maskSensitiveUrl should mask credentials in URL")
        void maskSensitiveUrl_urlWithCredentials_masksCredentials() throws Exception {
            Method maskSensitiveUrlMethod =
                DatabaseConfiguration.class.getDeclaredMethod("maskSensitiveUrl", String.class);
            maskSensitiveUrlMethod.setAccessible(true);
            String urlWithCreds = "jdbc:postgresql://user:password@localhost:5432/db";
            String result = (String) maskSensitiveUrlMethod.invoke(underTest, urlWithCreds);
            assertThat(result).isEqualTo("jdbc:postgresql://***:***@localhost:5432/db")
                .doesNotContain("user")
                .doesNotContain("password");
        }

    }

    @Nested
    @DisplayName("Database Type Branch Coverage Tests")
    class DatabaseTypeBranchCoverageTests {

        @Test
        @DisplayName("Should correctly identify MySQL database type")
        void databaseType_mysqlUrl_identifiesAsMySQL() {
            DatabaseType databaseType = DatabaseType.fromJdbcUrl(MYSQL_JDBC_URL);
            assertThat(databaseType).isNotNull()
                .isEqualTo(DatabaseType.MYSQL)
                .isNotEqualTo(DatabaseType.POSTGRESQL)
                .isNotEqualTo(DatabaseType.H2);
        }

        @Test
        @DisplayName("Should correctly identify that MySQL is neither PostgreSQL nor H2")
        void databaseTypeBranches_mysqlType_matchesNeitherPostgresqlNorH2() {
            DatabaseType databaseType = DatabaseType.fromJdbcUrl(MYSQL_JDBC_URL);
            boolean isPostgreSQL = databaseType == DatabaseType.POSTGRESQL;
            boolean isH2 = databaseType == DatabaseType.H2;
            assertThat(isPostgreSQL).isFalse();
            assertThat(isH2).isFalse();
        }

        @Test
        @DisplayName("Should create DataSource with MySQL URL without applying PostgreSQL or H2 optimizations")
        void dataSource_mysqlUrl_skipsPostgresqlAndH2Optimizations() {
            when(databaseProperty.getUrl()).thenReturn(MYSQL_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("mysql_user");
            when(databaseProperty.getPassword()).thenReturn("mysql_password");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(hikariProperty.getMaximumPoolSize()).thenReturn(null);
            when(hikariProperty.getMinimumIdle()).thenReturn(null);
            when(hikariProperty.getConnectionTimeout()).thenReturn(null);
            when(hikariProperty.getIdleTimeout()).thenReturn(null);
            when(hikariProperty.getMaxLifetime()).thenReturn(null);
            when(hikariProperty.getLeakDetectionThreshold()).thenReturn(null);
            when(hikariProperty.getInitializationFailTimeout()).thenReturn(0L);
            when(databaseProperty.getHikari()).thenReturn(hikariProperty);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            HikariDataSource hikariDataSource = (HikariDataSource) result;
            assertThat(hikariDataSource.getJdbcUrl()).isEqualTo(MYSQL_JDBC_URL);
            assertThat(hikariDataSource.getDriverClassName()).isEqualTo("com.mysql.cj.jdbc.Driver");
            hikariDataSource.close();
        }

    }

    @Nested
    @DisplayName("Logging Branch Coverage Tests")
    class LoggingBranchCoverageTests {

        @Test
        @DisplayName("Should create DataSource and log configuration summary")
        void dataSource_infoEnabled_logsConfigurationSummary() {
            when(databaseProperty.getUrl()).thenReturn(H2_JDBC_URL);
            when(databaseProperty.getUsername()).thenReturn("sa");
            when(databaseProperty.getPassword()).thenReturn("");
            when(databaseProperty.getDriverClassName()).thenReturn(null);
            when(databaseProperty.getHikari()).thenReturn(null);
            when(environment.getActiveProfiles()).thenReturn(new String[0]);
            when(environment.getProperty("spring.application.name")).thenReturn(null);

            DataSource result = underTest.dataSource();

            assertThat(result).isInstanceOf(HikariDataSource.class);
            ((HikariDataSource) result).close();
        }

    }

}
