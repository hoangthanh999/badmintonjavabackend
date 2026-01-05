package com.hoangthanhhong.badminton.service.base;

import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract String getEntityName();

    @Override
    public T create(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new ResourceNotFoundException(getEntityName() + " not found");
        }
        return getRepository().save(entity);
    }

    @Override
    public void delete(ID id) {
        T entity = findById(id);
        getRepository().delete(entity);
    }

    @Override
    public T findById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName() + " not found"));
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }
}
