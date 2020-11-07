package com.cqmike.core.annotation;

import java.lang.annotation.*;

/**
 * @author cq
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    QueryType value() default QueryType.eq;

}
