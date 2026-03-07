package com.weehong.bootstrap.database.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HikariProperty Tests")
class HikariPropertyTest {

    @Test
    @DisplayName("HikariProperty should be instantiable")
    void shouldBeInstantiable() {
        HikariProperty property = new HikariProperty();
        assertThat(property).isNotNull();
    }

    @Nested
    @DisplayName("Constructor and Instantiation Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create HikariProperty instance successfully")
        void testConstructor_createsInstance() {
            HikariProperty hikariProperty = new HikariProperty();

            assertThat(hikariProperty).isNotNull();
        }

        @Test
        @DisplayName("Should initialize with null values by default")
        void testConstructor_initializesWithNullValues() {
            HikariProperty hikariProperty = new HikariProperty();

            assertThat(hikariProperty.getMaximumPoolSize()).isNull();
            assertThat(hikariProperty.getMinimumIdle()).isNull();
            assertThat(hikariProperty.getIdleTimeout()).isNull();
            assertThat(hikariProperty.getMaxLifetime()).isNull();
            assertThat(hikariProperty.getConnectionTimeout()).isNull();
            assertThat(hikariProperty.getLeakDetectionThreshold()).isNull();
            assertThat(hikariProperty.getInitializationFailTimeout()).isNull();
        }

    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get maximumPoolSize")
        void testMaximumPoolSize_setAndGet() {
            HikariProperty hikariProperty = new HikariProperty();
            Integer expectedValue = 20;

            hikariProperty.setMaximumPoolSize(expectedValue);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should set and get minimumIdle")
        void testMinimumIdle_setAndGet() {
            HikariProperty hikariProperty = new HikariProperty();
            Integer expectedValue = 5;

            hikariProperty.setMinimumIdle(expectedValue);

            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should set and get idleTimeout")
        void testIdleTimeout_setAndGet() {
            HikariProperty hikariProperty = new HikariProperty();
            Long expectedValue = 600000L;

            hikariProperty.setIdleTimeout(expectedValue);

            assertThat(hikariProperty.getIdleTimeout()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should set and get maxLifetime")
        void testMaxLifetime_setAndGet() {
            HikariProperty hikariProperty = new HikariProperty();
            Long expectedValue = 1800000L;

            hikariProperty.setMaxLifetime(expectedValue);

            assertThat(hikariProperty.getMaxLifetime()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should set and get connectionTimeout")
        void testConnectionTimeout_setAndGet() {
            HikariProperty hikariProperty = new HikariProperty();
            Long expectedValue = 30000L;

            hikariProperty.setConnectionTimeout(expectedValue);

            assertThat(hikariProperty.getConnectionTimeout()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should set and get leakDetectionThreshold")
        void testLeakDetectionThreshold_setAndGet() {
            HikariProperty hikariProperty = new HikariProperty();
            Long expectedValue = 60000L;

            hikariProperty.setLeakDetectionThreshold(expectedValue);

            assertThat(hikariProperty.getLeakDetectionThreshold()).isEqualTo(expectedValue);
        }

    }

    @Nested
    @DisplayName("Null Value Handling Tests")
    class NullValueTests {

        @Test
        @DisplayName("Should allow setting maximumPoolSize to null")
        void testMaximumPoolSize_allowsNull() {
            HikariProperty hikariProperty = new HikariProperty();
            hikariProperty.setMaximumPoolSize(10);

            hikariProperty.setMaximumPoolSize(null);

            assertThat(hikariProperty.getMaximumPoolSize()).isNull();
        }

        @Test
        @DisplayName("Should allow setting minimumIdle to null")
        void testMinimumIdle_allowsNull() {
            HikariProperty hikariProperty = new HikariProperty();
            hikariProperty.setMinimumIdle(5);

            hikariProperty.setMinimumIdle(null);

            assertThat(hikariProperty.getMinimumIdle()).isNull();
        }

        @Test
        @DisplayName("Should allow setting idleTimeout to null")
        void testIdleTimeout_allowsNull() {
            HikariProperty hikariProperty = new HikariProperty();
            hikariProperty.setIdleTimeout(600000L);

            hikariProperty.setIdleTimeout(null);

            assertThat(hikariProperty.getIdleTimeout()).isNull();
        }

        @Test
        @DisplayName("Should allow setting maxLifetime to null")
        void testMaxLifetime_allowsNull() {
            HikariProperty hikariProperty = new HikariProperty();
            hikariProperty.setMaxLifetime(1800000L);

            hikariProperty.setMaxLifetime(null);

            assertThat(hikariProperty.getMaxLifetime()).isNull();
        }

        @Test
        @DisplayName("Should allow setting connectionTimeout to null")
        void testConnectionTimeout_allowsNull() {
            HikariProperty hikariProperty = new HikariProperty();
            hikariProperty.setConnectionTimeout(30000L);

            hikariProperty.setConnectionTimeout(null);

            assertThat(hikariProperty.getConnectionTimeout()).isNull();
        }

        @Test
        @DisplayName("Should allow setting leakDetectionThreshold to null")
        void testLeakDetectionThreshold_allowsNull() {
            HikariProperty hikariProperty = new HikariProperty();
            hikariProperty.setLeakDetectionThreshold(60000L);

            hikariProperty.setLeakDetectionThreshold(null);

            assertThat(hikariProperty.getLeakDetectionThreshold()).isNull();
        }

    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        // Note: HikariProperty is a simple POJO used for Spring configuration binding.
        // It stores values without validation - HikariCP validates at DataSource initialization.
        // These tests verify the POJO can hold boundary values that will be validated later.

        @Test
        @DisplayName("Should store boundary values for maximumPoolSize (validated by HikariCP at init)")
        void testMaximumPoolSize_storesBoundaryValues() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(1);
            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(1);

            hikariProperty.setMaximumPoolSize(100);
            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should store boundary values for minimumIdle (validated by HikariCP at init)")
        void testMinimumIdle_storesBoundaryValues() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMinimumIdle(0);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(0);

            hikariProperty.setMinimumIdle(10);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(10);
        }

        @Test
        @DisplayName("Should store zero values for idleTimeout and maxLifetime (valid in HikariCP to disable)")
        void testTimeouts_storeZeroValues() {
            HikariProperty hikariProperty = new HikariProperty();

            // idleTimeout=0 means idle connections are never removed
            hikariProperty.setIdleTimeout(0L);
            // maxLifetime=0 means infinite lifetime (not recommended but valid)
            hikariProperty.setMaxLifetime(0L);

            assertThat(hikariProperty.getIdleTimeout()).isEqualTo(0L);
            assertThat(hikariProperty.getMaxLifetime()).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should store typical timeout values")
        void testTimeouts_storeTypicalValues() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setConnectionTimeout(30000L);
            hikariProperty.setLeakDetectionThreshold(60000L);

            assertThat(hikariProperty.getConnectionTimeout()).isEqualTo(30000L);
            assertThat(hikariProperty.getLeakDetectionThreshold()).isEqualTo(60000L);
        }

        @Test
        @DisplayName("Should store maximum integer values for pool sizes")
        void testMaximumPoolSize_storesMaxInteger() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(Integer.MAX_VALUE);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("Should store large timeout values")
        void testTimeouts_storeLargeValues() {
            HikariProperty hikariProperty = new HikariProperty();

            // Store large but reasonable values (e.g., 24 hours in milliseconds)
            long twentyFourHours = 24 * 60 * 60 * 1000L;
            hikariProperty.setIdleTimeout(twentyFourHours);
            hikariProperty.setMaxLifetime(twentyFourHours);
            hikariProperty.setConnectionTimeout(twentyFourHours);
            hikariProperty.setLeakDetectionThreshold(twentyFourHours);

            assertThat(hikariProperty.getIdleTimeout()).isEqualTo(twentyFourHours);
            assertThat(hikariProperty.getMaxLifetime()).isEqualTo(twentyFourHours);
            assertThat(hikariProperty.getConnectionTimeout()).isEqualTo(twentyFourHours);
            assertThat(hikariProperty.getLeakDetectionThreshold()).isEqualTo(twentyFourHours);
        }

    }

    @Nested
    @DisplayName("Multiple Property Setting Tests")
    class MultiplePropertyTests {

        @Test
        @DisplayName("Should set and get all properties correctly")
        void testAllProperties_setAndGet() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(20);
            hikariProperty.setMinimumIdle(10);
            hikariProperty.setIdleTimeout(600000L);
            hikariProperty.setMaxLifetime(1800000L);
            hikariProperty.setConnectionTimeout(30000L);
            hikariProperty.setLeakDetectionThreshold(60000L);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(20);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(10);
            assertThat(hikariProperty.getIdleTimeout()).isEqualTo(600000L);
            assertThat(hikariProperty.getMaxLifetime()).isEqualTo(1800000L);
            assertThat(hikariProperty.getConnectionTimeout()).isEqualTo(30000L);
            assertThat(hikariProperty.getLeakDetectionThreshold()).isEqualTo(60000L);
        }

        @Test
        @DisplayName("Should handle property value updates correctly")
        void testProperties_handleUpdates() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(10);
            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(10);

            hikariProperty.setMaximumPoolSize(20);
            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(20);

            hikariProperty.setMaximumPoolSize(30);
            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should maintain independent property values")
        void testProperties_maintainIndependence() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(20);
            hikariProperty.setMinimumIdle(10);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(20);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(10);

            hikariProperty.setMaximumPoolSize(15);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(15);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(10);
        }

    }

    @Nested
    @DisplayName("Real-World Scenario Tests")
    class RealWorldScenarioTests {

        @Test
        @DisplayName("Should configure typical production HikariCP settings")
        void testTypicalProductionConfiguration() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(10);
            hikariProperty.setMinimumIdle(5);
            hikariProperty.setIdleTimeout(600000L);
            hikariProperty.setMaxLifetime(1800000L);
            hikariProperty.setConnectionTimeout(30000L);
            hikariProperty.setLeakDetectionThreshold(60000L);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(10);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(5);
            assertThat(hikariProperty.getIdleTimeout()).isEqualTo(600000L);
            assertThat(hikariProperty.getMaxLifetime()).isEqualTo(1800000L);
            assertThat(hikariProperty.getConnectionTimeout()).isEqualTo(30000L);
            assertThat(hikariProperty.getLeakDetectionThreshold()).isEqualTo(60000L);
        }

        @Test
        @DisplayName("Should configure high-throughput HikariCP settings")
        void testHighThroughputConfiguration() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(50);
            hikariProperty.setMinimumIdle(20);
            hikariProperty.setConnectionTimeout(10000L);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(50);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(20);
            assertThat(hikariProperty.getConnectionTimeout()).isEqualTo(10000L);
        }

        @Test
        @DisplayName("Should configure minimal HikariCP settings")
        void testMinimalConfiguration() {
            HikariProperty hikariProperty = new HikariProperty();

            hikariProperty.setMaximumPoolSize(5);
            hikariProperty.setMinimumIdle(2);

            assertThat(hikariProperty.getMaximumPoolSize()).isEqualTo(5);
            assertThat(hikariProperty.getMinimumIdle()).isEqualTo(2);
            assertThat(hikariProperty.getIdleTimeout()).isNull();
            assertThat(hikariProperty.getMaxLifetime()).isNull();
        }

    }

}
