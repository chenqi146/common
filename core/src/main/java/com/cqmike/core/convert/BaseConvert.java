package com.cqmike.core.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: iot
 * @ClassName: BaseConvert
 * @Description: 基础转换类  实体类和表单类转换
 * @Author: chen qi
 * @Date: 2020/2/22 14:39
 * @Version: 1.0
 **/
public abstract class BaseConvert<E, F> {

    private static final Logger log = LoggerFactory.getLogger(BaseConvert.class);

    protected Class<E> entityClass = (Class<E>) this.getSuperClassGenericType(this.getClass(), 0);
    protected Class<F> formClass = (Class<F>) this.getSuperClassGenericType(this.getClass(), 1);


    public BaseConvert() {
    }


    public F convertToForm(E entity) {
        if (entity == null) {
            return null;
        }
        F form = this.generateFormPojo();
        if (form == null) {
            //todo 动态生成类异常
        }
        this.doConvert(entity, form);
        this.afterConvertToForm(entity, form);

        return form;

    }

    protected void afterConvertToForm(E entity, F form) {
    }

    public E convertToEntity(F form) {
        if (form == null) {
            return null;
        }
        E entity = this.generateEntityPojo();
        if (entity == null) {
            //todo 动态生成类异常
        }

        this.doConvert(form, entity);
        this.afterConvertToEntity(form, entity);

        return entity;
    }

    protected void afterConvertToEntity(F form, E entity) {
    }

    public List<F> convertToFormList(List<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        List<F> result = new ArrayList<>();

        for (E entity : entities) {
            result.add(this.convertToForm(entity));
        }

        return result;
    }

    public List<E> convertToEntityList(List<F> forms) {
        if (CollectionUtils.isEmpty(forms)) {
            return Collections.emptyList();
        }
        List<E> result = new ArrayList<>();

        for (F form : forms) {
            result.add(this.convertToEntity(form));
        }

        return result;
    }


    public void doConvert(Object entity, Object form) {
        BeanUtils.copyProperties(entity, form);
    }

    public F generateFormPojo() {
        try {
            return this.formClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public E generateEntityPojo() {
        try {
            return this.entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        } else {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index < params.length && index >= 0) {
                if (!(params[index] instanceof Class)) {
                    log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
                    return Object.class;
                } else {
                    return (Class<?>) params[index];
                }
            } else {
                log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
                return Object.class;
            }
        }
    }

}
