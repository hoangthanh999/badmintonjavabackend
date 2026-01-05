package com.hoangthanhhong.badminton.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseService<T, ID> {
    T create(T entity);

    T update(ID id, T entity);

    void delete(ID id);

    T findById(ID id);

    List<T> findAll();

    Page<T> findAll(Pageable pageable);
}
