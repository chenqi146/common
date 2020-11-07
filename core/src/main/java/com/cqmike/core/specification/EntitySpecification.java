package com.cqmike.core.specification;

import com.cqmike.core.annotation.Query;
import com.cqmike.core.annotation.QueryType;
import com.cqmike.core.exception.BusinessException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: iot
 * @ClassName: EntitySpecification
 * @Description: 实体  S 为searchForm   T为实体
 * @Author: chen qi
 * @Date: 2020/2/21 10:38
 * @Version: 1.0
 **/
public class EntitySpecification<S, T> implements Specification<T> {

    private S s;

    public EntitySpecification(S s) {
        this.s = s;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        Class<?> clazz = s.getClass();

        List<Predicate> predicateList = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                String name = field.getName();

                PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
                Method readMethod = pd.getReadMethod();
                Object value = readMethod.invoke(s);

                if (value == null) {
                    continue;
                }
                Path<Object> tableName = root.get(name);

                // 获取注解  没有注解全部走equal
                Query queryAnnotation = field.getDeclaredAnnotation(Query.class);
                if (queryAnnotation == null) {
                    predicateList.add(cb.equal(tableName.as(String.class), value));
                    continue;
                }

                QueryType queryType = queryAnnotation.value();
                // todo 目前统一and连接  后续是否使用or等等待定

                if (value instanceof Date) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    value = dateFormat.format(value);
                }

                Predicate predicate = null;
                switch (queryType) {
                    case eq:
                        predicate = this.getEqual(cb, value, tableName);
                        break;
                    case ne:
                        predicate = this.getNotEqual(cb, value, tableName);
                        break;
                    case lt:
                        predicate = this.getLessThan(cb, value, tableName);
                        break;
                    case lte:
                        predicate = this.getLessThanOrEqualTo(cb, value, tableName);
                        break;
                    case gt:
                        predicate = this.getGreaterThan(cb, value, tableName);
                        break;
                    case gte:
                        predicate = this.getGreaterThanOrEqualTo(cb, value, tableName);
                        break;
                    case like:
                        predicate = this.getLike(cb, value, tableName);
                        break;
                    case in:
                        predicate = this.getIn(clazz.getName(), value, tableName);
                        break;
                    default:
                }
                predicateList.add(predicate);
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
                //todo 日志处理
                e.printStackTrace();
            }
        }

        Predicate[] predicates = predicateList.toArray(new Predicate[0]);

        return cb.and(predicates);
    }

    private Predicate getIn(String fieldName, Object value, Path<Object> tableName) {
        if (!(value instanceof List)) {
            throw new BusinessException("SearchForm: " + fieldName +
                    " @Query(value = QueryType.in) 所标注的字段类型错误");
        }
        return tableName.in(value);
    }

    private Predicate getLike(CriteriaBuilder cb, Object value, Path<Object> tableName) {
        return cb.like(tableName.as(String.class), '%' + (String) value + '%');
    }

    private Predicate getLessThanOrEqualTo(CriteriaBuilder cb, Object value, Path<Object> tableName) {
        return cb.lessThanOrEqualTo(tableName.as(String.class), (String) value);
    }

    private Predicate getGreaterThan(CriteriaBuilder cb, Object value, Path<Object> tableName) {
        return cb.greaterThan(tableName.as(String.class), (String) value);
    }

    private Predicate getGreaterThanOrEqualTo(CriteriaBuilder cb, Object value, Path<Object> tableName) {
        return cb.greaterThanOrEqualTo(tableName.as(String.class), (String) value);
    }

    private Predicate getLessThan(CriteriaBuilder cb, Object value, Path<Object> tableName) {
        return cb.lessThan(tableName.as(String.class), (String) value);
    }

    private Predicate getNotEqual(CriteriaBuilder cb, Object value, Path<Object> tableName) {
        return cb.notEqual(tableName, value);
    }

    private Predicate getEqual(CriteriaBuilder cb, Object value, Path<Object> tableName) {
        return cb.equal(tableName, value);
    }

    @Override
    public Specification<T> and(Specification<T> other) {
        return null;
    }

    @Override
    public Specification<T> or(Specification<T> other) {
        return null;
    }
}
