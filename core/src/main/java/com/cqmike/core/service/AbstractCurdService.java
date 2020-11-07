package com.cqmike.core.service;

import com.cqmike.core.convert.BaseConvert;
import com.cqmike.core.entity.BaseEntity;
import com.cqmike.core.exception.BusinessException;
import com.cqmike.core.form.BaseForm;
import com.cqmike.core.form.BaseSearchForm;
import com.cqmike.core.repository.BaseRepository;
import com.cqmike.core.specification.EntitySpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @program: autoexcel
 * @ClassName: AbstractCurdService
 * @Description: AbstractCurdService
 * @Author: chen qi
 * @Date: 2019/12/22 9:59
 * @Version: 1.0
 **/
public abstract class AbstractCurdService<T extends BaseEntity, ID, S extends BaseSearchForm, F extends BaseForm> implements CurdService<F, ID, S> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String entityName;

    protected final BaseRepository<T, ID> repository;

    private final BaseConvert<T, F> convert;

    protected AbstractCurdService(BaseRepository<T, ID> repository,
                                  BaseConvert<T, F> convert) {
        this.repository = repository;
        this.convert = convert;

        Class<T> entityClass = (Class<T>) fetchType(0);
        this.entityName = entityClass.getSimpleName();
    }

    /**
     * 获取实际的泛型类型。
     * @param index 泛型索引
     * @return 返回真正的泛型类型
     */
    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "type index must be between 0 to 1");

        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
    }

    @Override
    public List<F> listAllBySearchForm(S s) {
        EntitySpecification<S, T> specification = new EntitySpecification<>(s);
        List<T> all = repository.findAll(specification);
        if (CollectionUtils.isEmpty(all)) {
            return Collections.emptyList();
        }
        return this.convert.convertToFormList(all);
    }

    @Override
    public Page<F> listAllBySearchFormPage(S s) {
        EntitySpecification<S, T> specification = new EntitySpecification<>(s);
        Pageable pageable = PageRequest.of(s.getPage(), s.getSize());
        Page<T> all = repository.findAll(specification, pageable);
        return all.map(this.convert::convertToForm);
    }


    /**
     *  列出所有
     * @return
     */
    @Override
    public List<F> listAll() {
        List<T> list = repository.findAll();
        return this.convert.convertToFormList(list);
    }

    /**
     *   根据id集合查询
     * @param ids ids
     * @return List
     */
    @Override
    public List<F> listAllByIds(Collection<ID> ids) {
        return CollectionUtils.isEmpty(ids) ? Collections.emptyList() : this.convert.convertToFormList(repository.findAllById(ids));
    }

    /**
     *  根据id获取实体
     * @param id id
     * @return Optional
     */
    @Override
    public Optional<F> fetchById(ID id) {
        Assert.notNull(id, entityName + " id 不能为空");

        Optional<T> optional = repository.findById(id);
        return optional.map(this.convert::convertToForm);
    }

    /**
     * 根据主键查询
     * @param id id
     * @return entity
     */
    @Override
    public F findById(ID id) {

        return fetchById(id).orElseThrow(() -> new BusinessException(entityName + "没有找到"));
    }

    /**
     * 根据主键查询
     * @param id id
     * @return 允许为空
     */
    @Override
    public F findByIdOfNullable(ID id) {
        return fetchById(id).orElse(null);
    }

    /**
     *  count
     */
    @Override
    public long count() {
        return repository.count();
    }

    /**
     * 创建
     * @param form form
     * @return form
     */
    @Override
    public F create(F form) {
        Assert.notNull(form, entityName + " data不能为空");

        T entity = this.convert.convertToEntity(form);

        return this.convert.convertToForm(repository.save(entity));
    }

    /**
     *  批量创建
     * @param forms forms
     * @return List
     */
    @Override
    public List<F> createInBatch(Collection<F> forms) {
        List<T> entityList = this.convert.convertToEntityList((List<F>) forms);
        return CollectionUtils.isEmpty(forms) ? Collections.emptyList() : this.convert.convertToFormList(repository.saveAll(entityList));
    }

    /**
     *  修改
     * @param form form
     * @return form
     */
    @Override
    public F update(F form) {
        Assert.notNull(form, entityName + " data不能为空");
        T entity = this.convert.convertToEntity(form);
        return this.convert.convertToForm(repository.saveAndFlush(entity));
    }

    /**
     *  批量修改
     * @param forms forms
     * @return List
     */
    @Override
    public List<F> updateInBatch(Collection<F> forms) {
        List<T> list = this.convert.convertToEntityList((List<F>) forms);
        return CollectionUtils.isEmpty(forms) ? Collections.emptyList() : this.convert.convertToFormList(list);
    }

    /**
     *   主键删除
     * @param id id
     * @return entity
     */
    @Override
    public F removeById(ID id) {
        F entity = findById(id);

        remove(entity);

        return entity;
    }

    /**
     *  删除
     * @param id id
     * @return  允许为空
     */
    @Override
    public F removeByIdOfNullable(ID id) {
        return fetchById(id).map(form -> {
            remove(form);
            return form;
        }).orElse(null);
    }

    /**
     * 删除
     * @param form form
     */
    @Override
    public void remove(F form) {
        repository.delete(this.convert.convertToEntity(form));
    }

    /**
     * 根据id批量删除
     * @param ids ids
     */
    @Override
    public void removeInBatch(Collection<ID> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            log.warn(entityName + " id collection is empty");
            return;
        }
        repository.deleteByIdIn(ids);
    }

    /**
     * 根据实体集合删除
     * @param forms forms
     */
    @Override
    public void removeAll(Collection<F> forms) {
        if (CollectionUtils.isEmpty(forms)) {
            log.warn(entityName + " forms collection is empty");
            return;
        }
        repository.deleteInBatch(this.convert.convertToEntityList((List<F>) forms));
    }

    /**
     * 删除所有
     */
    @Override
    public void removeAll() {
        repository.deleteAll();
    }
}
