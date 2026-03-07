package com.weehong.bootstrap.core.helpers;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public final class ValidationHelper {

    private static final int DEFAULT_MAX_PAGE_SIZE = 1000;

    private ValidationHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static void validatePaginationParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }

        if (size <= 0 || size > DEFAULT_MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Size must be > 0 and <= " + DEFAULT_MAX_PAGE_SIZE);
        }
    }

    public static Sort.Direction parseSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return Sort.Direction.ASC;
        }

        String normalizedDirection = sortDirection.trim().toUpperCase();

        if ("ASC".equals(normalizedDirection)) {
            return Sort.Direction.ASC;
        } else if ("DESC".equals(normalizedDirection)) {
            return Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Invalid sortDirection: must be 'ASC' or 'DESC'");
        }
    }

    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number");
        }
    }

    public static void validateUuid(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
    }

    public static void validateId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(String.format("%s ID must be a positive number", entityName));
        }
    }

    public static void validateUuid(UUID uuid, String entityName) {
        if (uuid == null) {
            throw new IllegalArgumentException(String.format("%s UUID cannot be null", entityName));
        }
    }

    public static void validateSortBy(String sortBy, String... allowedFields) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            throw new IllegalArgumentException("sortBy cannot be null or empty");
        }

        String normalizedSortBy = sortBy.trim();

        for (String allowedField : allowedFields) {
            if (allowedField.equals(normalizedSortBy)) {
                return;
            }
        }

        throw new IllegalArgumentException(
            String.format("Invalid sortBy field: '%s'. Allowed fields: %s",
                sortBy, String.join(", ", allowedFields))
        );
    }

}
