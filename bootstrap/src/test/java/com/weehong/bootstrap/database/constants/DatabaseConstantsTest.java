package com.weehong.bootstrap.database.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DatabaseConstants Tests")
class DatabaseConstantsTest {

    @Test
    @DisplayName("DatabaseConstants class should be final")
    void classShouldBeFinal() {
        assertThat(isFinal(DatabaseConstants.class.getModifiers())).isTrue();
    }

    @Nested
    @DisplayName("Private Constructor Tests")
    class PrivateConstructorTests {

        @Test
        @DisplayName("Should throw AssertionError when constructor is invoked via reflection")
        void constructor_invokedViaReflection_throwsAssertionError() throws Exception {
            Constructor<DatabaseConstants> constructor = DatabaseConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(AssertionError.class);
        }

        @Test
        @DisplayName("Should have private constructor")
        void constructor_shouldBePrivate() throws Exception {
            Constructor<DatabaseConstants> constructor = DatabaseConstants.class.getDeclaredConstructor();

            assertThat(isPrivate(constructor.getModifiers())).isTrue();
        }

    }

    @Nested
    @DisplayName("Constant Values Tests")
    class ConstantValuesTests {

        @Test
        @DisplayName("Should have correct JDBC H2 prefix")
        void jdbcH2_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.JDBC_H2).isEqualTo("jdbc:h2:");
        }

        @Test
        @DisplayName("Should have correct JDBC PostgreSQL prefix")
        void jdbcPostgresql_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.JDBC_POSTGRESQL).isEqualTo("jdbc:postgresql:");
        }

        @Test
        @DisplayName("Should have correct JDBC MySQL prefix")
        void jdbcMysql_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.JDBC_MYSQL).isEqualTo("jdbc:mysql:");
        }

        @Test
        @DisplayName("Should have correct default pool name")
        void defaultPoolName_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.DEFAULT_POOL_NAME).isEqualTo("hikari-pool");
        }

        @Test
        @DisplayName("Should have correct default application name")
        void defaultApplicationName_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.DEFAULT_APPLICATION_NAME).isEqualTo("upmatches");
        }

        @Test
        @DisplayName("Should have correct default maximum pool size")
        void defaultMaximumPoolSize_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.DEFAULT_MAXIMUM_POOL_SIZE).isEqualTo(10);
        }

        @Test
        @DisplayName("Should have correct default minimum idle")
        void defaultMinimumIdle_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.DEFAULT_MINIMUM_IDLE).isEqualTo(10);
        }

        @Test
        @DisplayName("Should have correct default connection timeout")
        void defaultConnectionTimeout_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.DEFAULT_CONNECTION_TIMEOUT)
                .isEqualTo(TimeUnit.SECONDS.toMillis(30))
                .isEqualTo(30000L);
        }

        @Test
        @DisplayName("Should have correct default idle timeout")
        void defaultIdleTimeout_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.DEFAULT_IDLE_TIMEOUT)
                .isEqualTo(TimeUnit.MINUTES.toMillis(10))
                .isEqualTo(600000L);
        }

        @Test
        @DisplayName("Should have correct default max lifetime")
        void defaultMaxLifetime_shouldHaveCorrectValue() {
            assertThat(DatabaseConstants.DEFAULT_MAX_LIFETIME)
                .isEqualTo(TimeUnit.MINUTES.toMillis(30))
                .isEqualTo(1800000L);
        }

    }

    @Nested
    @DisplayName("Class Definition Tests")
    class ClassDefinitionTests {

        @Test
        @DisplayName("Should be a final class")
        void class_shouldBeFinal() {
            assertThat(isFinal(DatabaseConstants.class.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("Should have no declared public constructors")
        void class_shouldHaveNoPublicConstructors() {
            Constructor<?>[] constructors = DatabaseConstants.class.getConstructors();
            assertThat(constructors).isEmpty();
        }

    }

}
