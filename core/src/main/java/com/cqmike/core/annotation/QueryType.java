package com.cqmike.core.annotation;

/**
 * @program: iot
 * @EnumName: QueryType
 * @Description: 查询枚举
 * @Author: chen qi
 * @Date: 2020/2/20 21:53
 * @Version: 1.0
 **/
public enum QueryType {

    eq,
    like,
    lt,
    ne,
    gt,
    gte,
    lte,
    /**
     * 使用此类型时  字段类型必须为String[]
     * 并且查询的数据库字段名为属性字段名
     */
    in,
    ;

    private QueryType() {

    }


}
