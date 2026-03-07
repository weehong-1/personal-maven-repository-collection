package com.weehong.bootstrap.database.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_H2;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_MYSQL;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_POSTGRESQL;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DatabaseType Tests")
class DatabaseTypeTest {

    @Test
    @DisplayName("DatabaseType enum should have expected values")
    void shouldHaveExpectedValues() {
        assertThat(DatabaseType.values()).hasSize(3);
    }

    @Nested
    @DisplayName("getPrefix() Method Tests")
    class GetPrefixTests {

        @Test
        @DisplayName("Should return correct prefix for H2 database type")
        void testGetPrefix_h2_returnsCorrectPrefix() {
            String prefix = DatabaseType.H2.getPrefix();

            assertThat(prefix)
                .isNotNull()
                .isEqualTo(JDBC_H2)
                .isEqualTo("jdbc:h2:");
        }

        @Test
        @DisplayName("Should return correct prefix for PostgreSQL database type")
        void testGetPrefix_postgresql_returnsCorrectPrefix() {
            String prefix = DatabaseType.POSTGRESQL.getPrefix();

            assertThat(prefix)
                .isNotNull()
                .isEqualTo(JDBC_POSTGRESQL)
                .isEqualTo("jdbc:postgresql:");
        }

        @Test
        @DisplayName("Should return correct prefix for MySQL database type")
        void testGetPrefix_mysql_returnsCorrectPrefix() {
            String prefix = DatabaseType.MYSQL.getPrefix();

            assertThat(prefix)
                .isNotNull()
                .isEqualTo(JDBC_MYSQL)
                .isEqualTo("jdbc:mysql:");
        }

    }

    @Nested
    @DisplayName("getDriverClassName() Method Tests")
    class GetDriverClassNameTests {

        @Test
        @DisplayName("Should return correct driver class name for H2")
        void testGetDriverClassName_h2_returnsCorrectDriver() {
            String driverClassName = DatabaseType.H2.getDriverClassName();

            assertThat(driverClassName)
                .isNotNull()
                .isEqualTo("org.h2.Driver");
        }

        @Test
        @DisplayName("Should return correct driver class name for PostgreSQL")
        void testGetDriverClassName_postgresql_returnsCorrectDriver() {
            String driverClassName = DatabaseType.POSTGRESQL.getDriverClassName();

            assertThat(driverClassName)
                .isNotNull()
                .isEqualTo("org.postgresql.Driver");
        }

        @Test
        @DisplayName("Should return correct driver class name for MySQL")
        void testGetDriverClassName_mysql_returnsCorrectDriver() {
            String driverClassName = DatabaseType.MYSQL.getDriverClassName();

            assertThat(driverClassName)
                .isNotNull()
                .isEqualTo("com.mysql.cj.jdbc.Driver");
        }

    }

    @Nested
    @DisplayName("fromJdbcUrl() Method Tests")
    class FromJdbcUrlTests {

        @Test
        @DisplayName("Should return null when jdbcUrl is null")
        void testFromJdbcUrl_withNullUrl_returnsNull() {
            DatabaseType result = DatabaseType.fromJdbcUrl(null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return H2 database type for H2 JDBC URL")
        void testFromJdbcUrl_withH2Url_returnsH2() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:h2:mem:testdb");

            assertThat(result)
                .isNotNull()
                .isEqualTo(DatabaseType.H2);
        }

        @Test
        @DisplayName("Should return H2 database type for H2 file JDBC URL")
        void testFromJdbcUrl_withH2FileUrl_returnsH2() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:h2:file:/data/sample");

            assertThat(result)
                .isNotNull()
                .isEqualTo(DatabaseType.H2);
        }

        @Test
        @DisplayName("Should return PostgreSQL for PostgreSQL JDBC URL")
        void testFromJdbcUrl_withPostgresqlUrl_returnsPostgresql() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:postgresql://localhost:5432/mydb");

            assertThat(result)
                .isNotNull()
                .isEqualTo(DatabaseType.POSTGRESQL);
        }

        @Test
        @DisplayName("Should return PostgreSQL for PostgreSQL JDBC URL with parameters")
        void testFromJdbcUrl_withPostgresqlUrlAndParams_returnsPostgresql() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:postgresql://localhost:5432/mydb?ssl=true");

            assertThat(result)
                .isNotNull()
                .isEqualTo(DatabaseType.POSTGRESQL);
        }

        @Test
        @DisplayName("Should return MySQL for MySQL JDBC URL")
        void testFromJdbcUrl_withMysqlUrl_returnsMysql() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:mysql://localhost:3306/mydb");

            assertThat(result)
                .isNotNull()
                .isEqualTo(DatabaseType.MYSQL);
        }

        @Test
        @DisplayName("Should return MySQL for MySQL JDBC URL with SSL parameters")
        void testFromJdbcUrl_withMysqlUrlAndSsl_returnsMysql() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:mysql://localhost:3306/mydb?useSSL=true");

            assertThat(result)
                .isNotNull()
                .isEqualTo(DatabaseType.MYSQL);
        }

        @Test
        @DisplayName("Should return null for unknown JDBC URL")
        void testFromJdbcUrl_withUnknownUrl_returnsNull() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:oracle:thin:@localhost:1521:orcl");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for invalid JDBC URL format")
        void testFromJdbcUrl_withInvalidFormat_returnsNull() {
            DatabaseType result = DatabaseType.fromJdbcUrl("not-a-valid-jdbc-url");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for empty string JDBC URL")
        void testFromJdbcUrl_withEmptyString_returnsNull() {
            DatabaseType result = DatabaseType.fromJdbcUrl("");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for JDBC URL with correct prefix but incomplete")
        void testFromJdbcUrl_withPartialPrefix_handlesCorrectly() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:sqlserver://localhost:1433;database=mydb");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should handle case sensitivity correctly")
        void testFromJdbcUrl_withUpperCaseUrl_returnsNull() {
            DatabaseType result = DatabaseType.fromJdbcUrl("JDBC:H2:MEM:TESTDB");

            assertThat(result).isNull();
        }

    }

    @Nested
    @DisplayName("Parameterized Tests for All Database Types")
    class ParameterizedTests {

        static Stream<Arguments> databaseTypeParameters() {
            return Stream.of(
                Arguments.of(DatabaseType.H2, "jdbc:h2:", "org.h2.Driver", "jdbc:h2:mem:testdb"),
                Arguments.of(
                    DatabaseType.POSTGRESQL, "jdbc:postgresql:", "org.postgresql.Driver",
                    "jdbc:postgresql://localhost/db"),
                Arguments.of(
                    DatabaseType.MYSQL, "jdbc:mysql:", "com.mysql.cj.jdbc.Driver",
                    "jdbc:mysql://localhost/db")
            );
        }

        @ParameterizedTest(name = "{0} should have correct prefix and driver")
        @MethodSource("databaseTypeParameters")
        @DisplayName("Should verify all database types have correct configurations")
        void testAllDatabaseTypes_haveCorrectConfiguration(
            DatabaseType type,
            String expectedPrefix,
            String expectedDriver,
            String sampleUrl
        ) {
            assertThat(type.getPrefix()).isEqualTo(expectedPrefix);
            assertThat(type.getDriverClassName()).isEqualTo(expectedDriver);

            DatabaseType fromUrl = DatabaseType.fromJdbcUrl(sampleUrl);
            assertThat(fromUrl).isEqualTo(type);
        }

    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle JDBC URL with only prefix")
        void testFromJdbcUrl_withOnlyPrefix_matchesCorrectly() {
            assertThat(DatabaseType.fromJdbcUrl("jdbc:h2:")).isEqualTo(DatabaseType.H2);
            assertThat(DatabaseType.fromJdbcUrl("jdbc:postgresql:")).isEqualTo(DatabaseType.POSTGRESQL);
            assertThat(DatabaseType.fromJdbcUrl("jdbc:mysql:")).isEqualTo(DatabaseType.MYSQL);
        }

        @Test
        @DisplayName("Should verify all enum values are covered in values() iteration")
        void testFromJdbcUrl_iteratesAllEnumValues() {
            DatabaseType[] allTypes = DatabaseType.values();

            assertThat(allTypes)
                .hasSize(3)
                .contains(DatabaseType.H2, DatabaseType.POSTGRESQL, DatabaseType.MYSQL);

            for (DatabaseType type : allTypes) {
                DatabaseType matched = DatabaseType.fromJdbcUrl(type.getPrefix() + "test");
                assertThat(matched).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should handle whitespace in JDBC URL")
        void testFromJdbcUrl_withWhitespace_behavesAsExpected() {
            assertThat(DatabaseType.fromJdbcUrl(" jdbc:h2:mem:testdb")).isNull();

            assertThat(DatabaseType.fromJdbcUrl("jdbc:h2:mem:testdb ")).isEqualTo(DatabaseType.H2);
            assertThat(DatabaseType.fromJdbcUrl("jdbc:postgresql://localhost/db   "))
                .isEqualTo(DatabaseType.POSTGRESQL);
        }

    }

    @Nested
    @DisplayName("Enum Contract Tests")
    class EnumContractTests {

        @Test
        @DisplayName("Should have exactly 3 database types defined")
        void testEnumValues_hasThreeTypes() {
            DatabaseType[] values = DatabaseType.values();

            assertThat(values).hasSize(3);
        }

        @Test
        @DisplayName("Should support valueOf() for all enum constants")
        void testValueOf_worksForAllConstants() {
            assertThat(DatabaseType.valueOf("H2")).isEqualTo(DatabaseType.H2);
            assertThat(DatabaseType.valueOf("POSTGRESQL")).isEqualTo(DatabaseType.POSTGRESQL);
            assertThat(DatabaseType.valueOf("MYSQL")).isEqualTo(DatabaseType.MYSQL);
        }

        @Test
        @DisplayName("Should have unique prefixes for all database types")
        void testAllDatabaseTypes_haveUniquePrefixes() {
            String h2Prefix = DatabaseType.H2.getPrefix();
            String postgresPrefix = DatabaseType.POSTGRESQL.getPrefix();
            String mysqlPrefix = DatabaseType.MYSQL.getPrefix();

            assertThat(h2Prefix)
                .isNotEqualTo(postgresPrefix)
                .isNotEqualTo(mysqlPrefix);
            assertThat(postgresPrefix)
                .isNotEqualTo(mysqlPrefix);
        }

        @Test
        @DisplayName("Should have unique driver class names for all database types")
        void testAllDatabaseTypes_haveUniqueDrivers() {
            String h2Driver = DatabaseType.H2.getDriverClassName();
            String postgresDriver = DatabaseType.POSTGRESQL.getDriverClassName();
            String mysqlDriver = DatabaseType.MYSQL.getDriverClassName();

            assertThat(h2Driver)
                .isNotEqualTo(postgresDriver)
                .isNotEqualTo(mysqlDriver);
            assertThat(postgresDriver)
                .isNotEqualTo(mysqlDriver);
        }

    }

    @Nested
    @DisplayName("Null and Special Input Handling Tests")
    class NullAndSpecialInputTests {

        @ParameterizedTest
        @NullSource
        @DisplayName("Should return null for null JDBC URL")
        void testFromJdbcUrl_nullParameter_returnsNull(String jdbcUrl) {
            DatabaseType result = DatabaseType.fromJdbcUrl(jdbcUrl);

            assertThat(result).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "",
            "   ",
            "jdbc:",
            "jdbc",
            "random-string",
            "ftp://localhost",
            "http://localhost",
            "jdbc:unknown://localhost"
        })
        @DisplayName("Should return null for non-matching JDBC URLs")
        void testFromJdbcUrl_invalidUrls_returnNull(String invalidUrl) {
            DatabaseType result = DatabaseType.fromJdbcUrl(invalidUrl);

            assertThat(result).isNull();
        }

    }

    @Nested
    @DisplayName("Real-World JDBC URL Tests")
    class RealWorldJdbcUrlTests {

        @Test
        @DisplayName("Should match real H2 in-memory database URL")
        void testFromJdbcUrl_realH2InMemory_matches() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

            assertThat(result).isEqualTo(DatabaseType.H2);
        }

        @Test
        @DisplayName("Should match real H2 file database URL")
        void testFromJdbcUrl_realH2File_matches() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:h2:file:~/test;MODE=PostgreSQL");

            assertThat(result).isEqualTo(DatabaseType.H2);
        }

        @Test
        @DisplayName("Should match real PostgreSQL URL with connection pool parameters")
        void testFromJdbcUrl_realPostgresWithParams_matches() {
            DatabaseType result = DatabaseType.fromJdbcUrl(
                "jdbc:postgresql://localhost:5432/mydb?currentSchema=public&ssl=true&sslmode=require"
            );

            assertThat(result).isEqualTo(DatabaseType.POSTGRESQL);
        }

        @Test
        @DisplayName("Should match real MySQL URL with UTF-8 encoding")
        void testFromJdbcUrl_realMysqlWithEncoding_matches() {
            DatabaseType result = DatabaseType.fromJdbcUrl(
                "jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
            );

            assertThat(result).isEqualTo(DatabaseType.MYSQL);
        }

        @Test
        @DisplayName("Should match PostgreSQL URL with IPv6 address")
        void testFromJdbcUrl_postgresWithIpv6_matches() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:postgresql://[::1]:5432/mydb");

            assertThat(result).isEqualTo(DatabaseType.POSTGRESQL);
        }

        @Test
        @DisplayName("Should not match MariaDB JDBC URL (different from MySQL)")
        void testFromJdbcUrl_mariadb_returnsNull() {
            DatabaseType result = DatabaseType.fromJdbcUrl("jdbc:mariadb://localhost:3306/mydb");

            assertThat(result).isNull();
        }

    }

}
