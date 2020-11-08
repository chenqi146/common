package com.cqmike.base.service;

import com.cqmike.base.entity.BaseEntity;
import com.cqmike.base.exception.BusinessException;
import com.cqmike.base.repository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @program: base
 * @ClassName: AbstractCurdService
 * @Description: AbstractCurdService
 * @Author: chen qi
 * @Date: 2019/12/22 9:59
 * @Version: 1.0
 **/
public abstract class AbstractCurdService<E extends BaseEntity> implements CurdService<E> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String entityName;

    protected final BaseRepository<E, Long> repository;

    protected AbstractCurdService(BaseRepository<E, Long> repository) {
        this.repository = repository;

        Class<E> entityClass = (Class<E>) fetchType(0);
        this.entityName = entityClass.getSimpleName();
    }

    /**
     * 获取实际的泛型类型。
     *
     * @param index 泛型索引
     * @return 返回真正的泛型类型
     */
    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "type index must be between 0 to 1");
        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
    }

    @Override
    public List<E> findAll(Specification<E> specification, Sort sort) {
        return repository.findAll(specification, sort);
    }

    @Override
    public Page<E> findAll(Specification<E> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    /**
     * 列出所有
     *
     * @return
     */
    @NonNull
    @Override
    public List<E> findAll() {
        return repository.findAll();
    }

    /**
     * 根据id集合查询
     * @param ids ids
     * @return List
     */
    @NonNull
    @Override
    public List<E> findAllByIds(Collection<Long> ids) {
        return CollectionUtils.isEmpty(ids) ? Collections.emptyList() : repository.findAllById(ids);
    }

    /**
     * 根据id获取实体
     *
     * @param id id
     * @return Optional
     */
    @NonNull
    @Override
    public Optional<E> fetchById(@NonNull Long id) {
        return repository.findById(id);
    }

    /**
     * 根据主键查询
     *
     * @param id id
     * @return entity
     */
    @NonNull
    @Override
    public E findById(@NonNull Long id) {
        return fetchById(id).orElseThrow(() -> new BusinessException(entityName + "没有找到"));
    }

    /**
     * 根据主键查询
     *
     * @param id id
     * @return 允许为空
     */
    @Override
    public E findByIdOfNullable(@NonNull Long id) {
        return fetchById(id).orElse(null);
    }

    /**
     * count
     */
    @Override
    public long count() {
        return repository.count();
    }

    /**
     * 创建
     *
     * @param entity entity
     * @return entity
     */
    @NonNull
    @Override
    public E create(@NonNull E entity) {
        return repository.save(entity);
    }

    /**
     * 批量创建
     *
     * @param entitys entitys
     * @return List
     */
    @NonNull
    @Override
    public List<E> createInBatch(@NonNull Collection<E> entitys) {
        return CollectionUtils.isEmpty(entitys) ? Collections.emptyList() : repository.saveAll(entitys);
    }

    /**
     * 修改
     *
     * @param entity entity
     * @return entity
     */
    @NonNull
    @Override
    public E update(@NonNull E entity) {
        return repository.saveAndFlush(entity);
    }

    /**
     * 批量修改
     *
     * @param entitys entitys
     * @return List
     */
    @NonNull
    @Override
    public List<E> updateInBatch(@NonNull Collection<E> entitys) {
        return CollectionUtils.isEmpty(entitys) ? Collections.emptyList() : repository.saveAll(entitys);
    }

    /**
     * 主键删除
     *
     * @param id id
     * @return entity
     */
    @NonNull
    @Override
    public E removeById(@NonNull Long id) {
        E entity = findById(id);

        remove(entity);

        return entity;
    }

    /**
     * 删除
     *
     * @param id id
     * @return 允许为空
     */
    @Override
    public E removeByIdOfNullable(@NonNull Long id) {
        return fetchById(id).map(entity -> {
            remove(entity);
            return entity;
        }).orElse(null);
    }

    /**
     * 删除
     *
     * @param entity entity
     */
    @Override
    public void remove(@NonNull E entity) {
        repository.delete(entity);
    }

    /**
     * 根据id批量删除
     *
     * @param ids ids
     */
    @Override
    public void removeInBatch(@NonNull Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            log.warn(entityName + " id collection is empty");
            return;
        }
        repository.deleteByIdIn(ids);
    }

    /**
     * 根据实体集合删除
     *
     * @param entitys entitys
     */
    @Override
    public void removeAll(@NonNull Collection<E> entitys) {
        if (CollectionUtils.isEmpty(entitys)) {
            log.warn(entityName + " entitys collection is empty");
            return;
        }
        repository.deleteInBatch(entitys);
    }

    /**
     * 删除所有
     */
    @Override
    public void removeAll() {
        repository.deleteAll();
    }
}
