package com.weehong.bootstrap.core.responses;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PagedResponseTest {

    @Test
    @DisplayName("PagedResponse record should be instantiable")
    void shouldBeInstantiable() {
        List<String> content = List.of("item1");
        PageRequest pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(content, pageable, 1);
        PagedResponse<String> response = PagedResponse.of(page);
        assertThat(response).isNotNull();
    }

    @Nested
    @DisplayName("of(Page) factory method tests")
    class OfPageTests {

        @Test
        @DisplayName("Should create paged response from Page with content")
        void testOf_withContent() {
            List<String> content = List.of("item1", "item2", "item3");
            PageRequest pageable = PageRequest.of(0, 10);
            Page<String> page = new PageImpl<>(content, pageable, 100);

            PagedResponse<String> response = PagedResponse.of(page);

            assertThat(response.content()).hasSize(3);
            assertThat(response.content()).containsExactly("item1", "item2", "item3");
            assertThat(response.page().number()).isEqualTo(0);
            assertThat(response.page().size()).isEqualTo(10);
            assertThat(response.page().totalElements()).isEqualTo(100);
            assertThat(response.page().totalPages()).isEqualTo(10);
            assertThat(response.page().first()).isTrue();
            assertThat(response.page().last()).isFalse();
            assertThat(response.page().hasNext()).isTrue();
            assertThat(response.page().hasPrevious()).isFalse();
        }

        @Test
        @DisplayName("Should create paged response from empty Page")
        void testOf_emptyPage() {
            List<String> content = Collections.emptyList();
            PageRequest pageable = PageRequest.of(0, 10);
            Page<String> page = new PageImpl<>(content, pageable, 0);

            PagedResponse<String> response = PagedResponse.of(page);

            assertThat(response.content()).isEmpty();
            assertThat(response.page().number()).isEqualTo(0);
            assertThat(response.page().totalElements()).isEqualTo(0);
            assertThat(response.page().totalPages()).isEqualTo(0);
            assertThat(response.page().first()).isTrue();
            assertThat(response.page().last()).isTrue();
            assertThat(response.page().hasNext()).isFalse();
            assertThat(response.page().hasPrevious()).isFalse();
        }

        @Test
        @DisplayName("Should create paged response for middle page")
        void testOf_middlePage() {
            List<String> content = List.of("item1", "item2");
            PageRequest pageable = PageRequest.of(5, 2);
            Page<String> page = new PageImpl<>(content, pageable, 20);

            PagedResponse<String> response = PagedResponse.of(page);

            assertThat(response.page().number()).isEqualTo(5);
            assertThat(response.page().first()).isFalse();
            assertThat(response.page().last()).isFalse();
            assertThat(response.page().hasNext()).isTrue();
            assertThat(response.page().hasPrevious()).isTrue();
        }

        @Test
        @DisplayName("Should create paged response for last page")
        void testOf_lastPage() {
            List<String> content = List.of("item1");
            PageRequest pageable = PageRequest.of(9, 10);
            Page<String> page = new PageImpl<>(content, pageable, 91);

            PagedResponse<String> response = PagedResponse.of(page);

            assertThat(response.page().number()).isEqualTo(9);
            assertThat(response.page().first()).isFalse();
            assertThat(response.page().last()).isTrue();
            assertThat(response.page().hasNext()).isFalse();
            assertThat(response.page().hasPrevious()).isTrue();
        }

    }

    @Nested
    @DisplayName("of(Page, Function) factory method tests")
    class OfPageWithMapperTests {

        @Test
        @DisplayName("Should create paged response with mapped content")
        void testOf_withMapper() {
            List<Integer> content = List.of(1, 2, 3);
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Integer> page = new PageImpl<>(content, pageable, 3);

            PagedResponse<String> response = PagedResponse.of(page, num -> "Number: " + num);

            assertThat(response.content()).hasSize(3);
            assertThat(response.content()).containsExactly("Number: 1", "Number: 2", "Number: 3");
            assertThat(response.page().number()).isEqualTo(0);
            assertThat(response.page().totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should create paged response with complex mapping")
        void testOf_withComplexMapping() {
            record Source(String name, int value) {

            }

            record Target(String displayName) {

            }

            List<Source> content = List.of(
                new Source("first", 1),
                new Source("second", 2)
            );
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Source> page = new PageImpl<>(content, pageable, 2);

            PagedResponse<Target> response = PagedResponse.of(
                page,
                source -> new Target(source.name() + "-" + source.value())
            );

            assertThat(response.content()).hasSize(2);
            assertThat(response.content().get(0).displayName()).isEqualTo("first-1");
            assertThat(response.content().get(1).displayName()).isEqualTo("second-2");
        }

        @Test
        @DisplayName("Should create paged response from empty page with mapper")
        void testOf_emptyPageWithMapper() {
            List<Integer> content = Collections.emptyList();
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Integer> page = new PageImpl<>(content, pageable, 0);

            PagedResponse<String> response = PagedResponse.of(page, Object::toString);

            assertThat(response.content()).isEmpty();
            assertThat(response.page().totalElements()).isEqualTo(0);
        }

    }

    @Nested
    @DisplayName("PageInfo record tests")
    class PageInfoTests {

        @Test
        @DisplayName("Should create PageInfo with all fields")
        void testPageInfo_constructor() {
            PagedResponse.PageInfo pageInfo = new PagedResponse.PageInfo(
                5, 20, 1000L, 50, false, false, true, true
            );

            assertThat(pageInfo.number()).isEqualTo(5);
            assertThat(pageInfo.size()).isEqualTo(20);
            assertThat(pageInfo.totalElements()).isEqualTo(1000L);
            assertThat(pageInfo.totalPages()).isEqualTo(50);
            assertThat(pageInfo.first()).isFalse();
            assertThat(pageInfo.last()).isFalse();
            assertThat(pageInfo.hasNext()).isTrue();
            assertThat(pageInfo.hasPrevious()).isTrue();
        }

    }

    @Nested
    @DisplayName("Record constructor tests")
    class RecordConstructorTests {

        @Test
        @DisplayName("Should create paged response using record constructor")
        void testRecordConstructor() {
            List<String> content = List.of("item1", "item2");
            PagedResponse.PageInfo pageInfo = new PagedResponse.PageInfo(
                0, 10, 2L, 1, true, true, false, false
            );

            PagedResponse<String> response = new PagedResponse<>(content, pageInfo);

            assertThat(response.content()).hasSize(2);
            assertThat(response.page()).isEqualTo(pageInfo);
        }

    }

}
