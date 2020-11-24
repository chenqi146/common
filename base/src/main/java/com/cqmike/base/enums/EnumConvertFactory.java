package com.cqmike.base.enums;

import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @program: common
 * @description: 枚举转换
 * @author: chen qi
 * @create: 2020-11-24 21:58
 **/
public class EnumConvertFactory implements ConverterFactory<String , IEnum<?>> {

    private static final Map<Class<?>, Converter<String, ?>> converterMap = new WeakHashMap<>();

    @Override
    public <T extends IEnum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        Converter result = converterMap.get(targetType);
        if(result == null) {
            result = new StringToIEum<>(targetType);
            converterMap.put(targetType, result);
        }
        return result;
    }

    static class StringToIEum<T extends IEnum<?>> implements Converter<String, T> {
            private final Map<String, T> enumMap = new HashMap<>();

        public StringToIEum(Class<T> enumType) {
            T[] enums = enumType.getEnumConstants();
            for(T e : enums) {
                enumMap.put(e.getId() + "", e);
            }

        }
        @Override
        public T convert(String source) {
            T result = enumMap.get(source);
            if(result == null) {
                throw new IllegalArgumentException("No element matches " + source);
            }
            return result;
        }

    }


}
