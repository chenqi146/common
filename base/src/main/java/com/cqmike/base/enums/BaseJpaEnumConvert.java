package com.cqmike.base.enums;

import javax.persistence.AttributeConverter;
import java.util.Objects;

/**
 * @program: common
 * @description:  枚举内新建静态内部类继承此类,jpa字段上@Convert选枚举内部转换静态类
 * @author: chen qi
 * @create: 2020-11-24 22:16
 **/
public abstract class BaseJpaEnumConvert<S extends IEnum<?>, T> implements AttributeConverter<S, T> {

    private final Class<S> clazz;

    public BaseJpaEnumConvert(Class<S> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convertToDatabaseColumn(S attribute) {
        return attribute != null ? (T) attribute.getId() : null;
    }

    @Override
    public S convertToEntityAttribute(T dbData) {
        if (dbData == null) return null;

        S[] enums = clazz.getEnumConstants();

        for (S e : enums) {
            if (Objects.equals(dbData, e.getId())) {
                return e;
            }
        }
        throw new UnsupportedOperationException("枚举转化异常, 枚举【" + clazz.getSimpleName() + "】,数据库库中的值为：【" + dbData + "】");
    }

}
