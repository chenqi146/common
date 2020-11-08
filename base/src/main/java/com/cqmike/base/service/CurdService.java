package com.cqmike.base.service;

import com.cqmike.base.entity.BaseEntity;
import com.cqmike.base.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @program: common
 * @Interface: CurdService  T实体  ID 主键
 * @Description: curd接口类
 * @Author: chen qi
 * @Date: 2020-11-07
 * @Version: 1.0
 **/
@Validated
public interface CurdService<E extends BaseEntity> {

    /**
     * 实体类查询
     * @return
     */
    List<E> findAll(Specification<E> specification, Sort sort);

    /**
     * 分页查询
     * @return
     */
    Page<E> findAll(Specification<E> specification, Pageable pageable);

    /**
     * List All
     *
     * @return List
     */
    @NonNull
    List<E> findAll();

    /**
     * List all by ids
     *
     * @param ids ids
     * @return List
     */
    @NonNull
    List<E> findAllByIds(@Nullable Collection<Long> ids);

    /**
     * 通过id查询
     *
     * @param id id
     * @return Optional
     */
    @NonNull
    Optional<E> fetchById(@NonNull Long id);

    /**
     * id查询
     *
     * @param id id
     * @return E
     * @throws BusinessException 不存在
     */
    @NonNull
    E findById(@NonNull Long id);

    /**
     * 查询允许为空
     *
     * @param id id
     * @return E
     */
    @Nullable
    E findByIdOfNullable(@NonNull Long id);

    /**
     * count
     *
     * @return long
     */
    long count();

    /**
     * create
     *
     * @param form form
     * @return E
     */
    @NonNull
    @Transactional
    E create(@NonNull E form);

    /**
     * save list
     *
     * @param forms forms
     * @return List
     */
    @NonNull
    @Transactional
    List<E> createInBatch(@NonNull Collection<E> forms);

    /**
     * Updates
     *
     * @param form form
     * @return E
     */
    @NonNull
    @Transactional
    E update(@NonNull E form);


    /**
     * Updates by entities
     *
     * @param forms forms
     * @return List
     */
    @NonNull
    @Transactional
    List<E> updateInBatch(@NonNull Collection<E> forms);

    /**
     * Removes by id
     *
     * @param id id
     * @return E
     */
    @NonNull
    @Transactional
    E removeById(@NonNull Long id);

    /**
     * Removes by id if present.
     *
     * @param id id
     * @return E
     */
    @Nullable
    @Transactional
    E removeByIdOfNullable(@NonNull Long id);

    /**
     * Remove by form
     *
     * @param form form
     */
    @Transactional
    void remove(@NonNull E form);

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Transactional
    void removeInBatch(@NonNull Collection<Long> ids);

    /**
     * Remove all by entities
     *
     * @param forms forms
     */
    @Transactional
    void removeAll(@NonNull Collection<E> forms);

    /**
     * Remove all
     */
    @Transactional
    void removeAll();
}
