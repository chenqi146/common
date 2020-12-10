package com.cqmike.base.auth;

import cn.hutool.core.collection.CollUtil;
import com.cqmike.base.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: common
 * @description: 用户私有变量
 * @author: chen qi
 * @create: 2020-11-08 08:07
 **/
public final class Auth {

    private static final ThreadLocal<Map<String, Object>> map = new InheritableThreadLocal<>();

    public static final String USER = "user";

    public static <T> T get(String key, Class<T> tClass) {
        Map<String, Object> objectMap = map.get();
        if (CollUtil.isEmpty(objectMap)) {
            return null;
        }
        Object v = objectMap.get(key);
        return JsonUtils.parse(JsonUtils.toJson(v), tClass);
    }

    public static <T> T get(String key, TypeReference<T> type) {
        Map<String, Object> objectMap = map.get();
        if (CollUtil.isEmpty(objectMap)) {
            return null;
        }
        Object v = objectMap.get(key);
        return JsonUtils.parse(JsonUtils.toJson(v), type);
    }

    public static void put(String key, Object v) {
        Map<String, Object> objectMap = map.get();
        if (CollUtil.isEmpty(objectMap)) {
            objectMap = new HashMap<>();
        }
        objectMap.put(key, v);
        map.set(objectMap);
    }

    public static Object remove(String key) {
        Map<String, Object> objectMap = map.get();
        if (CollUtil.isEmpty(objectMap)) {
            return null;
        }
        return objectMap.remove(key);
    }

    public static void clear() {
        map.remove();
    }

}
