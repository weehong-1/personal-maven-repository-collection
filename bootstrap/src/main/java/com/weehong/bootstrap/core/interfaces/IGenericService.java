package com.weehong.bootstrap.core.interfaces;

import com.weehong.bootstrap.core.responses.PagedResponse;

import java.util.UUID;

public interface IGenericService<T, K> {

    PagedResponse<K> getAll(int page, int size, String sortBy, String sortDirection);

    K getById(Long id);

    K getByUuid(UUID uuid);

    K save(T entity);

    K update(Long id, T entity);

    K updateByUuid(UUID uuid, T entity);

    void softDelete(Long id);

    void softDeleteByUuid(UUID uuid);

    void delete(Long id);

    void deleteByUuid(UUID uuid);

}
