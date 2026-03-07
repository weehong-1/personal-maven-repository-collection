package com.weehong.bootstrap.database.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DatabaseProperty Tests")
class DatabasePropertyTest {

    @Test
    @DisplayName("DatabaseProperty should be instantiable")
    void shouldBeInstantiable() {
        DatabaseProperty property = new DatabaseProperty();
        assertThat(property).isNotNull();
    }

    @Nested
    @DisplayName("Constructor and Instantiation Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create DatabaseProperty instance successfully")
        void testConstructor_createsInstance() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            assertThat(databaseProperty).isNotNull();
        }

        @Test
        @DisplayName("Should initialize HikariProperty automatically")
        void testConstructor_initializesHikariProperty() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            assertThat(databaseProperty.getHikari()).isNotNull();
            assertThat(databaseProperty.getHikari()).isInstanceOf(HikariProperty.class);
        }

        @Test
        @DisplayName("Should initialize database fields with null values")
        void testConstructor_initializesFieldsWithNull() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            assertThat(databaseProperty.getUrl()).isNull();
            assertThat(databaseProperty.getUsername()).isNull();
            assertThat(databaseProperty.getPassword()).isNull();
            assertThat(databaseProperty.getDriverClassName()).isNull();
        }

    }

    @Nested
    @DisplayName("HikariProperty Nested Configuration Tests")
    class HikariPropertyTests {

        @Test
        @DisplayName("Should get pre-initialized HikariProperty")
        void testGetHikari_returnsInitializedProperty() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            HikariProperty hikari = databaseProperty.getHikari();

            assertThat(hikari).isNotNull();
        }

        @Test
        @DisplayName("Should maintain reference to HikariProperty")
        void testHikari_maintainsReference() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            HikariProperty hikari = databaseProperty.getHikari();

            hikari.setMaximumPoolSize(15);

            assertThat(databaseProperty.getHikari().getMaximumPoolSize()).isEqualTo(15);
        }

        @Test
        @DisplayName("Should support configuring nested HikariProperty values")
        void testHikari_supportsNestedConfiguration() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.getHikari().setMaximumPoolSize(10);
            databaseProperty.getHikari().setMinimumIdle(5);
            databaseProperty.getHikari().setConnectionTimeout(30000L);

            assertThat(databaseProperty.getHikari().getMaximumPoolSize()).isEqualTo(10);
            assertThat(databaseProperty.getHikari().getMinimumIdle()).isEqualTo(5);
            assertThat(databaseProperty.getHikari().getConnectionTimeout()).isEqualTo(30000L);
        }

    }

    @Nested
    @DisplayName("Database URL Tests")
    class UrlTests {

        @Test
        @DisplayName("Should set and get URL")
        void testUrl_setAndGet() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String url = "jdbc:postgresql://localhost:5432/mydb";

            databaseProperty.setUrl(url);

            assertThat(databaseProperty.getUrl()).isEqualTo(url);
        }

        @Test
        @DisplayName("Should handle H2 database URL")
        void testUrl_handlesH2Database() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String url = "jdbc:h2:mem:testdb";

            databaseProperty.setUrl(url);

            assertThat(databaseProperty.getUrl()).isEqualTo(url);
        }

        @Test
        @DisplayName("Should handle MySQL database URL")
        void testUrl_handlesMySqlDatabase() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String url = "jdbc:mysql://localhost:3306/mydb";

            databaseProperty.setUrl(url);

            assertThat(databaseProperty.getUrl()).isEqualTo(url);
        }

        @Test
        @DisplayName("Should handle PostgreSQL database URL with parameters")
        void testUrl_handlesPostgreSqlWithParams() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String url = "jdbc:postgresql://localhost:5432/mydb?ssl=true&sslmode=require";

            databaseProperty.setUrl(url);

            assertThat(databaseProperty.getUrl()).isEqualTo(url);
        }

        @Test
        @DisplayName("Should allow setting URL to null")
        void testUrl_allowsNull() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            databaseProperty.setUrl("jdbc:postgresql://localhost:5432/mydb");

            databaseProperty.setUrl(null);

            assertThat(databaseProperty.getUrl()).isNull();
        }

    }

    @Nested
    @DisplayName("Username Tests")
    class UsernameTests {

        @Test
        @DisplayName("Should set and get username")
        void testUsername_setAndGet() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String username = "dbuser";

            databaseProperty.setUsername(username);

            assertThat(databaseProperty.getUsername()).isEqualTo(username);
        }

        @Test
        @DisplayName("Should handle empty username")
        void testUsername_handlesEmpty() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUsername("");

            assertThat(databaseProperty.getUsername()).isEqualTo("");
        }

        @Test
        @DisplayName("Should allow setting username to null")
        void testUsername_allowsNull() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            databaseProperty.setUsername("admin");

            databaseProperty.setUsername(null);

            assertThat(databaseProperty.getUsername()).isNull();
        }

    }

    @Nested
    @DisplayName("Password Tests")
    class PasswordTests {

        @Test
        @DisplayName("Should set and get password")
        void testPassword_setAndGet() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String password = "securePassword123";

            databaseProperty.setPassword(password);

            assertThat(databaseProperty.getPassword()).isEqualTo(password);
        }

        @Test
        @DisplayName("Should handle complex passwords")
        void testPassword_handlesComplexPasswords() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String password = "P@ssw0rd!#$%^&*()_+-=[]{}|;:',.<>?/~`";

            databaseProperty.setPassword(password);

            assertThat(databaseProperty.getPassword()).isEqualTo(password);
        }

        @Test
        @DisplayName("Should handle empty password")
        void testPassword_handlesEmpty() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setPassword("");

            assertThat(databaseProperty.getPassword()).isEqualTo("");
        }

        @Test
        @DisplayName("Should allow setting password to null")
        void testPassword_allowsNull() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            databaseProperty.setPassword("password123");

            databaseProperty.setPassword(null);

            assertThat(databaseProperty.getPassword()).isNull();
        }

    }

    @Nested
    @DisplayName("Driver Class Name Tests")
    class DriverClassNameTests {

        @Test
        @DisplayName("Should set and get driver class name")
        void testDriverClassName_setAndGet() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String driverClassName = "org.postgresql.Driver";

            databaseProperty.setDriverClassName(driverClassName);

            assertThat(databaseProperty.getDriverClassName()).isEqualTo(driverClassName);
        }

        @Test
        @DisplayName("Should handle H2 driver class name")
        void testDriverClassName_handlesH2Driver() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String driverClassName = "org.h2.Driver";

            databaseProperty.setDriverClassName(driverClassName);

            assertThat(databaseProperty.getDriverClassName()).isEqualTo(driverClassName);
        }

        @Test
        @DisplayName("Should handle MySQL driver class name")
        void testDriverClassName_handlesMySqlDriver() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            String driverClassName = "com.mysql.cj.jdbc.Driver";

            databaseProperty.setDriverClassName(driverClassName);

            assertThat(databaseProperty.getDriverClassName()).isEqualTo(driverClassName);
        }

        @Test
        @DisplayName("Should allow setting driver class name to null")
        void testDriverClassName_allowsNull() {
            DatabaseProperty databaseProperty = new DatabaseProperty();
            databaseProperty.setDriverClassName("org.postgresql.Driver");

            databaseProperty.setDriverClassName(null);

            assertThat(databaseProperty.getDriverClassName()).isNull();
        }

    }

    @Nested
    @DisplayName("Multiple Property Configuration Tests")
    class MultiplePropertyTests {

        @Test
        @DisplayName("Should set and get all properties correctly")
        void testAllProperties_setAndGet() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUrl("jdbc:postgresql://localhost:5432/mydb");
            databaseProperty.setUsername("admin");
            databaseProperty.setPassword("secret");
            databaseProperty.setDriverClassName("org.postgresql.Driver");
            databaseProperty.getHikari().setMaximumPoolSize(10);

            assertThat(databaseProperty.getUrl()).isEqualTo("jdbc:postgresql://localhost:5432/mydb");
            assertThat(databaseProperty.getUsername()).isEqualTo("admin");
            assertThat(databaseProperty.getPassword()).isEqualTo("secret");
            assertThat(databaseProperty.getDriverClassName()).isEqualTo("org.postgresql.Driver");
            assertThat(databaseProperty.getHikari().getMaximumPoolSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("Should maintain independent property values")
        void testProperties_maintainIndependence() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUrl("jdbc:postgresql://localhost:5432/db1");
            databaseProperty.setUsername("user1");

            assertThat(databaseProperty.getUrl()).isEqualTo("jdbc:postgresql://localhost:5432/db1");
            assertThat(databaseProperty.getUsername()).isEqualTo("user1");

            databaseProperty.setUrl("jdbc:postgresql://localhost:5432/db2");

            assertThat(databaseProperty.getUrl()).isEqualTo("jdbc:postgresql://localhost:5432/db2");
            assertThat(databaseProperty.getUsername()).isEqualTo("user1");
        }

        @Test
        @DisplayName("Should handle property value updates correctly")
        void testProperties_handleUpdates() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUsername("user1");
            assertThat(databaseProperty.getUsername()).isEqualTo("user1");

            databaseProperty.setUsername("user2");
            assertThat(databaseProperty.getUsername()).isEqualTo("user2");

            databaseProperty.setUsername("user3");
            assertThat(databaseProperty.getUsername()).isEqualTo("user3");
        }

    }

    @Nested
    @DisplayName("Real-World Configuration Scenarios")
    class RealWorldScenarioTests {

        @Test
        @DisplayName("Should configure PostgreSQL database with full settings")
        void testPostgreSqlConfiguration() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUrl("jdbc:postgresql://localhost:5432/production_db");
            databaseProperty.setUsername("prod_user");
            databaseProperty.setPassword("prod_password");
            databaseProperty.setDriverClassName("org.postgresql.Driver");
            databaseProperty.getHikari().setMaximumPoolSize(20);
            databaseProperty.getHikari().setMinimumIdle(10);
            databaseProperty.getHikari().setConnectionTimeout(30000L);

            assertThat(databaseProperty.getUrl()).contains("postgresql");
            assertThat(databaseProperty.getUsername()).isEqualTo("prod_user");
            assertThat(databaseProperty.getDriverClassName()).isEqualTo("org.postgresql.Driver");
            assertThat(databaseProperty.getHikari().getMaximumPoolSize()).isEqualTo(20);
        }

        @Test
        @DisplayName("Should configure H2 in-memory database")
        void testH2InMemoryConfiguration() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
            databaseProperty.setUsername("sa");
            databaseProperty.setPassword("");
            databaseProperty.setDriverClassName("org.h2.Driver");

            assertThat(databaseProperty.getUrl()).contains("h2:mem");
            assertThat(databaseProperty.getUsername()).isEqualTo("sa");
            assertThat(databaseProperty.getPassword()).isEqualTo("");
        }

        @Test
        @DisplayName("Should configure MySQL database with UTF-8 support")
        void testMySqlConfigurationWithUtf8() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUrl("jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=UTF-8");
            databaseProperty.setUsername("mysql_user");
            databaseProperty.setPassword("mysql_pass");
            databaseProperty.setDriverClassName("com.mysql.cj.jdbc.Driver");

            assertThat(databaseProperty.getUrl()).contains("mysql");
            assertThat(databaseProperty.getUrl()).contains("useUnicode=true");
            assertThat(databaseProperty.getDriverClassName()).isEqualTo("com.mysql.cj.jdbc.Driver");
        }

        @Test
        @DisplayName("Should configure database with custom HikariCP pool settings")
        void testCustomHikariPoolConfiguration() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUrl("jdbc:postgresql://localhost:5432/mydb");
            databaseProperty.getHikari().setMaximumPoolSize(50);
            databaseProperty.getHikari().setMinimumIdle(25);
            databaseProperty.getHikari().setIdleTimeout(600000L);
            databaseProperty.getHikari().setMaxLifetime(1800000L);
            databaseProperty.getHikari().setConnectionTimeout(30000L);
            databaseProperty.getHikari().setLeakDetectionThreshold(60000L);

            assertThat(databaseProperty.getHikari().getMaximumPoolSize()).isEqualTo(50);
            assertThat(databaseProperty.getHikari().getMinimumIdle()).isEqualTo(25);
            assertThat(databaseProperty.getHikari().getIdleTimeout()).isEqualTo(600000L);
            assertThat(databaseProperty.getHikari().getMaxLifetime()).isEqualTo(1800000L);
            assertThat(databaseProperty.getHikari().getConnectionTimeout()).isEqualTo(30000L);
            assertThat(databaseProperty.getHikari().getLeakDetectionThreshold()).isEqualTo(60000L);
        }

        @Test
        @DisplayName("Should handle minimal database configuration")
        void testMinimalConfiguration() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            databaseProperty.setUrl("jdbc:h2:mem:testdb");
            databaseProperty.setDriverClassName("org.h2.Driver");

            assertThat(databaseProperty.getUrl()).isNotNull();
            assertThat(databaseProperty.getDriverClassName()).isNotNull();
            assertThat(databaseProperty.getUsername()).isNull();
            assertThat(databaseProperty.getPassword()).isNull();
        }

    }

    @Nested
    @DisplayName("Integration Tests with Spring Configuration Properties")
    class SpringConfigurationTests {

        @Test
        @DisplayName("Should support being instantiated as Spring component")
        void testSpringComponentInstantiation() {
            DatabaseProperty databaseProperty = new DatabaseProperty();

            assertThat(databaseProperty).isNotNull();
            assertThat(databaseProperty.getClass().getAnnotation(Component.class)).isNotNull();
        }

        @Test
        @DisplayName("Should have ConfigurationProperties annotation with correct prefix")
        void testConfigurationPropertiesAnnotation() {
            ConfigurationProperties annotation =
                DatabaseProperty.class.getAnnotation(ConfigurationProperties.class);

            assertThat(annotation).isNotNull();
            assertThat(annotation.prefix()).isEqualTo("spring.datasource");
        }

        @Test
        @DisplayName("Should have NestedConfigurationProperty for hikari field")
        void testNestedConfigurationProperty() throws NoSuchFieldException {
            Field hikariField = DatabaseProperty.class.getDeclaredField("hikari");
            NestedConfigurationProperty annotation =
                hikariField.getAnnotation(NestedConfigurationProperty.class);

            assertThat(annotation).isNotNull();
        }

    }

}
