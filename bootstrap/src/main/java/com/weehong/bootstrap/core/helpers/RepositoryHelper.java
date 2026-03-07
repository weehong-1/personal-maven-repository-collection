package com.weehong.bootstrap.core.helpers;

import com.weehong.bootstrap.core.exceptions.types.ResourceNotFoundException;
import com.weehong.bootstrap.core.repositories.UuidRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public final class RepositoryHelper {

    private RepositoryHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static <T, I> T findByIdOrThrow(
        JpaRepository<T, I> repository,
        I id,
        Class<T> entityClass) {

        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                entityClass.getSimpleName() + " not found with ID: " + id));
    }

    public static <T> T findByUuidOrThrow(
        UuidRepository<T> repository,
        UUID uuid,
        Class<T> entityClass) {

        return repository.findByUuid(uuid)
            .orElseThrow(() -> new ResourceNotFoundException(
                entityClass.getSimpleName() + " not found with UUID: " + uuid));
    }

}
