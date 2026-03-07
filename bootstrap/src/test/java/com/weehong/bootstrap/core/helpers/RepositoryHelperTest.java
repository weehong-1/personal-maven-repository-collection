package com.weehong.bootstrap.core.helpers;

import com.weehong.bootstrap.core.exceptions.types.ResourceNotFoundException;
import com.weehong.bootstrap.core.repositories.UuidRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryHelperTest {

    @Mock
    private JpaRepository<TestEntity, Long> jpaRepository;

    @Mock
    private UuidRepository<TestEntity> uuidRepository;

    @Test
    @DisplayName("Should throw IllegalStateException when trying to instantiate")
    void testConstructor_throwsException() {
        assertThatThrownBy(() -> {
            Constructor<?> constructor = RepositoryHelper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        })
            .isInstanceOf(InvocationTargetException.class)
            .hasCauseInstanceOf(IllegalStateException.class)
            .cause()
            .hasMessageContaining("Utility class");
    }

    private record TestEntity(Long id, String name) {

    }

    @Nested
    @DisplayName("findByIdOrThrow() tests")
    class FindByIdOrThrowTests {

        @Test
        @DisplayName("Should return entity when found by ID")
        void testFindByIdOrThrow_whenEntityExists_returnsEntity() {
            Long id = 1L;
            TestEntity expectedEntity = new TestEntity(id, "Test");

            when(jpaRepository.findById(id)).thenReturn(Optional.of(expectedEntity));

            TestEntity result = RepositoryHelper.findByIdOrThrow(jpaRepository, id, TestEntity.class);

            assertThat(result).isEqualTo(expectedEntity);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when entity not found by ID")
        void testFindByIdOrThrow_whenEntityNotFound_throwsException() {
            Long id = 999L;

            when(jpaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                RepositoryHelper.findByIdOrThrow(jpaRepository, id, TestEntity.class)
            )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestEntity not found with ID: 999");
        }

        @Test
        @DisplayName("Should include entity class name in exception message")
        void testFindByIdOrThrow_exceptionContainsEntityClassName() {
            Long id = 42L;

            when(jpaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                RepositoryHelper.findByIdOrThrow(jpaRepository, id, TestEntity.class)
            )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestEntity")
                .hasMessageContaining("42");
        }

    }

    @Nested
    @DisplayName("findByUuidOrThrow() tests")
    class FindByUuidOrThrowTests {

        @Test
        @DisplayName("Should return entity when found by UUID")
        void testFindByUuidOrThrow_whenEntityExists_returnsEntity() {
            UUID uuid = UUID.randomUUID();
            TestEntity expectedEntity = new TestEntity(1L, "Test");

            when(uuidRepository.findByUuid(uuid)).thenReturn(Optional.of(expectedEntity));

            TestEntity result = RepositoryHelper.findByUuidOrThrow(uuidRepository, uuid, TestEntity.class);

            assertThat(result).isEqualTo(expectedEntity);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when entity not found by UUID")
        void testFindByUuidOrThrow_whenEntityNotFound_throwsException() {
            UUID uuid = UUID.randomUUID();

            when(uuidRepository.findByUuid(uuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                RepositoryHelper.findByUuidOrThrow(uuidRepository, uuid, TestEntity.class)
            )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestEntity not found with UUID: " + uuid);
        }

        @Test
        @DisplayName("Should include entity class name and UUID in exception message")
        void testFindByUuidOrThrow_exceptionContainsEntityClassNameAndUuid() {
            UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

            when(uuidRepository.findByUuid(uuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                RepositoryHelper.findByUuidOrThrow(uuidRepository, uuid, TestEntity.class)
            )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestEntity")
                .hasMessageContaining("550e8400-e29b-41d4-a716-446655440000");
        }

    }

}
