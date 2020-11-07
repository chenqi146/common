package com.cqmike.core.service;

import com.cqmike.core.form.BaseForm;
import com.cqmike.core.form.BaseSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @program: autoexcel
 * @Interface: CurdService  T实体  ID 主键
 * @Description: curd接口类
 * @Author: chen qi
 * @Date: 2019/12/22 9:37
 * @Version: 1.0
 **/
public interface CurdService<F extends BaseForm, ID, S extends BaseSearchForm> {

    /**
     *  实体类查询
     * @param s
     * @return
     */
    List<F> listAllBySearchForm(S s);

    /**
     *  分页查询
     * @param s
     * @return
     */
    Page<F> listAllBySearchFormPage(S s);

    /**
     *  List All
     * @return List
     */
    @NonNull
    List<F> listAll();

    /**
     * List all by ids
     *
     * @param ids ids
     * @return List
     */
    @NonNull
    List<F> listAllByIds(@Nullable Collection<ID> ids);

    /**
     *  通过id查询
     * @param id id
     * @return Optional
     */
    @NonNull
    Optional<F> fetchById(@NonNull ID id);

    /**
     * id查询
     * @param id id
     * @return F
     * @throws NotFoundException 不存在
     */
    @NonNull
    F findById(@NonNull ID id);

    /**
     * 查询允许为空
     * @param id id
     * @return F
     */
    @Nullable
    F findByIdOfNullable(@NonNull ID id);

    /**
     * count
     * @return long
     */
    long count();


    // todo 异常类未指定
    /**
     * create
     * @param form form
     * @return F
     */
    @NonNull
    @Transactional
    F create(@NonNull F form);

    /**
     * save list
     * @param forms forms
     * @return List
     */
    @NonNull
    @Transactional
    List<F> createInBatch(@NonNull Collection<F> forms);

    /**
     * Updates
     * @param form form
     * @return F
     */
    @NonNull
    @Transactional
    F update(@NonNull F form);


    /**
     * Updates by entities
     *
     * @param forms forms
     * @return List
     */
    @NonNull
    @Transactional
    List<F> updateInBatch(@NonNull Collection<F> forms);

    /**
     * Removes by id
     *
     * @param id id
     * @return F
     * @throws NotFoundException If the specified id does not exist
     */
    @NonNull
    @Transactional
    F removeById(@NonNull ID id);

    /**
     * Removes by id if present.
     *
     * @param id id
     * @return F
     */
    @Nullable
    @Transactional
    F removeByIdOfNullable(@NonNull ID id);

    /**
     * Remove by form
     *
     * @param form form
     */
    @Transactional
    void remove(@NonNull F form);

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Transactional
    void removeInBatch(@NonNull Collection<ID> ids);

    /**
     * Remove all by entities
     *
     * @param forms forms
     */
    @Transactional
    void removeAll(@NonNull Collection<F> forms);

    /**
     * Remove all
     */
    @Transactional
    void removeAll();
}
