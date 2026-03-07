package com.weehong.bootstrap.core.helpers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationHelperTest {

    @Test
    @DisplayName("Should throw IllegalStateException when trying to instantiate")
    void testConstructor_throwsException() {
        assertThatThrownBy(() -> {
            Constructor<?> constructor = ValidationHelper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        })
            .isInstanceOf(InvocationTargetException.class)
            .hasCauseInstanceOf(IllegalStateException.class)
            .cause()
            .hasMessageContaining("Utility class");
    }

    @Nested
    @DisplayName("validatePaginationParameters() tests")
    class ValidatePaginationParametersTests {

        @Test
        @DisplayName("Should accept valid pagination parameters")
        void testValidatePaginationParameters_valid() {
            ValidationHelper.validatePaginationParameters(0, 10);
            ValidationHelper.validatePaginationParameters(5, 100);
            ValidationHelper.validatePaginationParameters(0, 1000);
        }

        @Test
        @DisplayName("Should throw exception for negative page")
        void testValidatePaginationParameters_negativePage() {
            assertThatThrownBy(() -> ValidationHelper.validatePaginationParameters(-1, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page must be >= 0");
        }

        @Test
        @DisplayName("Should throw exception for zero size")
        void testValidatePaginationParameters_zeroSize() {
            assertThatThrownBy(() -> ValidationHelper.validatePaginationParameters(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Size must be > 0 and <= 1000");
        }

        @Test
        @DisplayName("Should throw exception for negative size")
        void testValidatePaginationParameters_negativeSize() {
            assertThatThrownBy(() -> ValidationHelper.validatePaginationParameters(0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Size must be > 0 and <= 1000");
        }

        @Test
        @DisplayName("Should throw exception for size exceeding max")
        void testValidatePaginationParameters_sizeExceedsMax() {
            assertThatThrownBy(() -> ValidationHelper.validatePaginationParameters(0, 1001))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Size must be > 0 and <= 1000");
        }

    }

    @Nested
    @DisplayName("parseSortDirection() tests")
    class ParseSortDirectionTests {

        @Test
        @DisplayName("Should return ASC for null input")
        void testParseSortDirection_null() {
            Sort.Direction result = ValidationHelper.parseSortDirection(null);

            assertThat(result).isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("Should return ASC for empty input")
        void testParseSortDirection_empty() {
            Sort.Direction result = ValidationHelper.parseSortDirection("");

            assertThat(result).isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("Should return ASC for whitespace input")
        void testParseSortDirection_whitespace() {
            Sort.Direction result = ValidationHelper.parseSortDirection("   ");

            assertThat(result).isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("Should return ASC for 'ASC' input")
        void testParseSortDirection_asc() {
            Sort.Direction result = ValidationHelper.parseSortDirection("ASC");

            assertThat(result).isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("Should return ASC for lowercase 'asc' input")
        void testParseSortDirection_ascLowercase() {
            Sort.Direction result = ValidationHelper.parseSortDirection("asc");

            assertThat(result).isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("Should return DESC for 'DESC' input")
        void testParseSortDirection_desc() {
            Sort.Direction result = ValidationHelper.parseSortDirection("DESC");

            assertThat(result).isEqualTo(Sort.Direction.DESC);
        }

        @Test
        @DisplayName("Should return DESC for lowercase 'desc' input")
        void testParseSortDirection_descLowercase() {
            Sort.Direction result = ValidationHelper.parseSortDirection("desc");

            assertThat(result).isEqualTo(Sort.Direction.DESC);
        }

        @Test
        @DisplayName("Should handle input with whitespace")
        void testParseSortDirection_withWhitespace() {
            Sort.Direction result = ValidationHelper.parseSortDirection("  DESC  ");

            assertThat(result).isEqualTo(Sort.Direction.DESC);
        }

        @Test
        @DisplayName("Should throw exception for invalid direction")
        void testParseSortDirection_invalid() {
            assertThatThrownBy(() -> ValidationHelper.parseSortDirection("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sortDirection: must be 'ASC' or 'DESC'");
        }

    }

    @Nested
    @DisplayName("validateId(Long) tests")
    class ValidateIdTests {

        @Test
        @DisplayName("Should accept valid positive ID")
        void testValidateId_valid() {
            ValidationHelper.validateId(1L);
            ValidationHelper.validateId(100L);
            ValidationHelper.validateId(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("Should throw exception for null ID")
        void testValidateId_null() {
            assertThatThrownBy(() -> ValidationHelper.validateId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID must be a positive number");
        }

        @Test
        @DisplayName("Should throw exception for zero ID")
        void testValidateId_zero() {
            assertThatThrownBy(() -> ValidationHelper.validateId(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID must be a positive number");
        }

        @Test
        @DisplayName("Should throw exception for negative ID")
        void testValidateId_negative() {
            assertThatThrownBy(() -> ValidationHelper.validateId(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID must be a positive number");
        }

    }

    @Nested
    @DisplayName("validateUuid(UUID) tests")
    class ValidateUuidTests {

        @Test
        @DisplayName("Should accept valid UUID")
        void testValidateUuid_valid() {
            ValidationHelper.validateUuid(UUID.randomUUID());
        }

        @Test
        @DisplayName("Should throw exception for null UUID")
        void testValidateUuid_null() {
            assertThatThrownBy(() -> ValidationHelper.validateUuid(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID cannot be null");
        }

    }

    @Nested
    @DisplayName("validateId(Long, String) tests")
    class ValidateIdWithEntityNameTests {

        @Test
        @DisplayName("Should accept valid positive ID with entity name")
        void testValidateIdWithEntityName_valid() {
            ValidationHelper.validateId(1L, "User");
        }

        @Test
        @DisplayName("Should throw exception with entity name for null ID")
        void testValidateIdWithEntityName_null() {
            assertThatThrownBy(() -> ValidationHelper.validateId(null, "User"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must be a positive number");
        }

        @Test
        @DisplayName("Should throw exception with entity name for zero ID")
        void testValidateIdWithEntityName_zero() {
            assertThatThrownBy(() -> ValidationHelper.validateId(0L, "Order"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order ID must be a positive number");
        }

        @Test
        @DisplayName("Should throw exception with entity name for negative ID")
        void testValidateIdWithEntityName_negative() {
            assertThatThrownBy(() -> ValidationHelper.validateId(-5L, "Product"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product ID must be a positive number");
        }

    }

    @Nested
    @DisplayName("validateUuid(UUID, String) tests")
    class ValidateUuidWithEntityNameTests {

        @Test
        @DisplayName("Should accept valid UUID with entity name")
        void testValidateUuidWithEntityName_valid() {
            ValidationHelper.validateUuid(UUID.randomUUID(), "Session");
        }

        @Test
        @DisplayName("Should throw exception with entity name for null UUID")
        void testValidateUuidWithEntityName_null() {
            assertThatThrownBy(() -> ValidationHelper.validateUuid(null, "Transaction"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction UUID cannot be null");
        }

    }

    @Nested
    @DisplayName("validateSortBy() tests")
    class ValidateSortByTests {

        @Test
        @DisplayName("Should accept valid sortBy field")
        void testValidateSortBy_valid() {
            ValidationHelper.validateSortBy("name", "name", "email", "createdAt");
            ValidationHelper.validateSortBy("email", "name", "email", "createdAt");
            ValidationHelper.validateSortBy("createdAt", "name", "email", "createdAt");
        }

        @Test
        @DisplayName("Should accept sortBy with single allowed field")
        void testValidateSortBy_singleAllowedField() {
            ValidationHelper.validateSortBy("id", "id");
        }

        @Test
        @DisplayName("Should throw exception for null sortBy")
        void testValidateSortBy_null() {
            assertThatThrownBy(() -> ValidationHelper.validateSortBy(null, "name", "email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sortBy cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception for empty sortBy")
        void testValidateSortBy_empty() {
            assertThatThrownBy(() -> ValidationHelper.validateSortBy("", "name", "email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sortBy cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception for whitespace sortBy")
        void testValidateSortBy_whitespace() {
            assertThatThrownBy(() -> ValidationHelper.validateSortBy("   ", "name", "email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sortBy cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception for invalid sortBy field")
        void testValidateSortBy_invalid() {
            assertThatThrownBy(() -> ValidationHelper.validateSortBy("invalid", "name", "email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sortBy field: 'invalid'")
                .hasMessageContaining("Allowed fields: name, email");
        }

        @Test
        @DisplayName("Should handle sortBy with leading/trailing whitespace")
        void testValidateSortBy_withWhitespace() {
            ValidationHelper.validateSortBy("  name  ", "name", "email");
        }

    }

}
