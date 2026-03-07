package com.weehong.bootstrap.core.responses;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("ApiResponse record should be instantiable")
    void shouldBeInstantiable() {
        ApiResponse<String> response = ApiResponse.success("test", "message", "/path");
        assertThat(response).isNotNull();
    }

    @Nested
    @DisplayName("success() factory method tests")
    class SuccessFactoryMethodTests {

        @Test
        @DisplayName("Should create successful response with data and message")
        void testSuccess_withDataAndMessage() {
            String testData = "Test Data";
            String message = "Operation successful";
            String path = "/api/test";

            Instant before = Instant.now();
            ApiResponse<String> response = ApiResponse.success(testData, message, path);
            Instant after = Instant.now();

            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo(testData);
            assertThat(response.message()).isEqualTo(message);
            assertThat(response.path()).isEqualTo(path);
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.timestamp()).isBetween(before, after);
        }

        @Test
        @DisplayName("Should create successful response with null data")
        void testSuccess_withNullData() {
            String message = "No data";
            String path = "/api/test";

            ApiResponse<Object> response = ApiResponse.success(null, message, path);

            assertThat(response.success()).isTrue();
            assertThat(response.data()).isNull();
            assertThat(response.message()).isEqualTo(message);
            assertThat(response.path()).isEqualTo(path);
        }

        @Test
        @DisplayName("Should create successful response with complex object")
        void testSuccess_withComplexObject() {
            record TestObject(String name, int value) {

            }

            TestObject testObject = new TestObject("test", 123);
            String message = "Success";
            String path = "/api/complex";

            ApiResponse<TestObject> response = ApiResponse.success(testObject, message, path);

            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo(testObject);
            assertThat(response.data().name()).isEqualTo("test");
            assertThat(response.data().value()).isEqualTo(123);
        }

    }

    @Nested
    @DisplayName("error() factory method tests")
    class ErrorFactoryMethodTests {

        @Test
        @DisplayName("Should create error response with message")
        void testError_withMessage() {
            String message = "Something went wrong";
            String path = "/api/test";

            Instant before = Instant.now();
            ApiResponse<Object> response = ApiResponse.error(message, path);
            Instant after = Instant.now();

            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.message()).isEqualTo(message);
            assertThat(response.path()).isEqualTo(path);
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.timestamp()).isBetween(before, after);
        }

        @Test
        @DisplayName("Should create error response with null message")
        void testError_withNullMessage() {
            String path = "/api/test";

            ApiResponse<Object> response = ApiResponse.error(null, path);

            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.message()).isNull();
        }

    }

    @Nested
    @DisplayName("Record constructor tests")
    class RecordConstructorTests {

        @Test
        @DisplayName("Should create response using record constructor")
        void testRecordConstructor() {
            Instant timestamp = Instant.now();
            String data = "test";
            String message = "message";
            String path = "/path";

            ApiResponse<String> response = new ApiResponse<>(true, data, message, timestamp, path);

            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo(data);
            assertThat(response.message()).isEqualTo(message);
            assertThat(response.timestamp()).isEqualTo(timestamp);
            assertThat(response.path()).isEqualTo(path);
        }

    }

}
