package com.cqmike.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cqmike.base.exception.RedisException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: common
 * @description:
 * @author: chen qi
 * @create: 2020-12-06 10:11
 **/
public class RedisClient {
    private static final Logger log = LoggerFactory.getLogger(RedisClient.class);


    private final RedisTemplate<String, Object> redisTemplate;

    public RedisClient(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    // =============================common============================

    public Set<String> scan(String keyPattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder()
                    .match(keyPattern)
                    .count(1000).build())) {
                while (cursor.hasNext()) {
                    keysTmp.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.error("redis error", e);
                throw new RuntimeException(e);
            }
            return keysTmp;
        });
    }


    public boolean hasKey(String key) {
        try {
            return Optional.ofNullable(redisTemplate.hasKey(key)).orElse(Boolean.FALSE);
        } catch (Exception e) {
            log.error("hasKey: key={}", key, e);
            throw new RedisException(e);
        }
    }


    public Set<String> keys(String key) {
        try {
            return redisTemplate.keys(key);
        } catch (Exception e) {
            log.error("keys: key={}", key, e);
            throw new RedisException(e);
        }
    }


    public boolean del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                return Optional.ofNullable(redisTemplate.delete(key[0])).orElse(Boolean.FALSE);
            } else {
                return Optional.ofNullable(redisTemplate.delete(CollectionUtils.arrayToList(key))).orElse(0L) > 0;
            }
        }
        return false;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */

    public boolean expire(String key, int time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("expire: key={}, time={}", key, time, e);
            throw new RedisException(e);
        }
    }


    public void rename(String key, String newKey) {
        redisTemplate.rename(key, newKey);
    }


    public long ttl(String key) {
        return Optional.ofNullable(redisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
    }


    public long append(String key, String value) {
        return Optional.ofNullable(redisTemplate.opsForValue().append(key, value)).orElse(0);
    }


    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (Objects.equals(value.getClass(), clazz)) {
            return clazz.cast(value);
        }
        return Jackson2JsonRedisSerializer.deserialize(value, clazz);
    }

    public <T> T get(String key, TypeReference<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return Jackson2JsonRedisSerializer.deserialize(value, type);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("set: key = {}, value = {}", key, value, e);
            throw new RedisException(e);
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */

    public void setex(String key, Object value, int time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
        } catch (Exception e) {
            log.error("setex: key = {}, value = {}", key, value, e);
            throw new RedisException(e);
        }
    }


    public long incr(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().increment(key, 1)).orElse(0L);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */

    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return Optional.ofNullable(redisTemplate.opsForValue().increment(key, delta)).orElse(0L);
    }


    public long decr(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().increment(key, -1)).orElse(0L);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */

    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return Optional.ofNullable(redisTemplate.opsForValue().increment(key, -delta)).orElse(0L);
    }


    public <T> T getSet(String key, T value) {
        Object oldValue = redisTemplate.opsForValue().getAndSet(key, value);
        if (oldValue == null) {
            return null;
        }
        Class<T> clazz = (Class<T>) value.getClass();
        return Jackson2JsonRedisSerializer.deserialize(oldValue, clazz);
    }


    public String mset(String... keysValues) {
        if (keysValues.length % 2 != 0) {
            throw new RedisException(" wrong number of arguments for MSET; keysValues = " + keysValues);
        }
        Map<String, Object> kvMap = new HashMap<>();
        for (int i = 0; i < keysValues.length; i += 2) {
            kvMap.put(keysValues[i], keysValues[i + 1]);
        }
        redisTemplate.opsForValue().multiSet(kvMap);
        return null;
    }


    public <T> void mset(String[] keys, T[] values) {
        if (keys == null || values == null || keys.length != values.length) {
            throw new RedisException("wrong number of arguments for MSET; keys = " + keys + "values = " + values);
        }
        Map<String, Object> kvMap = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            kvMap.put(keys[i], values[i]);
        }
        redisTemplate.opsForValue().multiSet(kvMap);
    }


    public <T> Map<String, T> mget(String[] keys, Class<T> clazz) {
        List<Object> stringValues = redisTemplate.opsForValue().multiGet(Arrays.asList(keys));
        if (stringValues == null) {
            return null;
        }
        Map<String, T> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.length; i++) {
            Object value = stringValues.get(i);
            if (value == null || StrUtil.isEmpty(value.toString())) continue;
            map.put(keys[i], Jackson2JsonRedisSerializer.deserialize(value, clazz));
        }
        return map;
    }

    public <T> Map<String, T> mget(String[] keys, TypeReference<T> type) {
        List<Object> stringValues = redisTemplate.opsForValue().multiGet(Arrays.asList(keys));
        if (stringValues == null) {
            return null;
        }
        Map<String, T> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.length; i++) {
            Object value = stringValues.get(i);
            if (value == null || StrUtil.isEmpty(value.toString())) continue;
            map.put(keys[i], Jackson2JsonRedisSerializer.deserialize(value, type));
        }
        return map;
    }


    // ================================Map=================================


    public boolean hexists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }


    public void hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("hset: key={}, field={}, value={} -- {}", key, field, value, e);
            throw new RedisException(e);
        }
    }


    public <T> T hget(String key, String item, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(key, item);
        if (value == null) {
            return null;
        }
        return Jackson2JsonRedisSerializer.deserialize(value, clazz);
    }

    public <T> T hget(String key, String item, TypeReference<T> type) {
        Object value = redisTemplate.opsForHash().get(key, item);
        if (value == null) {
            return null;
        }
        return Jackson2JsonRedisSerializer.deserialize(value, type);
    }


    public Set<String> hkeys(String key) {
        Set<Object> keys = redisTemplate.opsForHash().keys(key);
        Set<String> strKeys = new HashSet<>();
        keys.forEach(k -> strKeys.add(k.toString()));
        return strKeys;
    }


    public <T> Map<String, T> hgetAll(String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (CollUtil.isEmpty(entries)) {
            return Collections.emptyMap();
        }
        Map<String, T> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), Jackson2JsonRedisSerializer.deserialize(v, clazz)));
        return result;
    }


    public <T> Map<String, T> hgetAll(String key, TypeReference<T> type) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (CollUtil.isEmpty(entries)) {
            return Collections.emptyMap();
        }
        Map<String, T> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), Jackson2JsonRedisSerializer.deserialize(v, type)));
        return result;
    }

    public <T> List<T> hvals(String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (CollUtil.isEmpty(entries)) {
            return Collections.emptyList();
        }
        List<T> values = new ArrayList<>();
        entries.forEach((k, v) -> values.add(Jackson2JsonRedisSerializer.deserialize(v, clazz)));
        return values;
    }

    public <T> List<T> hvals(String key, TypeReference<T> type) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (CollUtil.isEmpty(entries)) {
            return Collections.emptyList();
        }
        List<T> values = new ArrayList<>();
        entries.forEach((k, v) -> values.add(Jackson2JsonRedisSerializer.deserialize(v, type)));
        return values;
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */

    public <T> Map<String, T> hmget(String key, Class<T> clazz, String... fields) {
        List<Object> values = redisTemplate.opsForHash().multiGet(key, Arrays.asList(fields));
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyMap();
        }
        Map<String, T> result = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            Object objJson = values.get(i);
            if (objJson == null) continue;
            result.put(fields[i], Jackson2JsonRedisSerializer.deserialize(objJson, clazz));
        }
        return result;
    }

    public <T> Map<String, T> hmget(String key, TypeReference<T> type, String... fields) {
        List<Object> values = redisTemplate.opsForHash().multiGet(key, Arrays.asList(fields));
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyMap();
        }
        Map<String, T> result = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            Object objJson = values.get(i);
            if (objJson == null) continue;
            result.put(fields[i], Jackson2JsonRedisSerializer.deserialize(objJson, type));
        }
        return result;
    }

    /**
     * 删除hash表中的值
     *
     * @param key   键 不能为null
     * @param field 项 可以使多个 不能为null
     */

    public boolean hdel(String key, String field) {
        Long delete = redisTemplate.opsForHash().delete(key, field);
        return delete > 0;
    }


    public long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }


    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("redis error", e);
            return Collections.emptySet();
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Optional.ofNullable(redisTemplate.opsForSet().isMember(key, value)).orElse(Boolean.FALSE);
        } catch (Exception e) {
            log.error("redis error", e);
            return false;
        }
    }


    public <T> Set<T> sdiff(Class<T> clazz, String... keys) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
        list.remove(0);
        Set<Object> difference = redisTemplate.opsForSet().difference(keys[0], list);
        if (difference == null) return null;
        Set<T> result = new HashSet<>();
        difference.forEach(e -> {
            result.add(Jackson2JsonRedisSerializer.deserialize(e, clazz));
        });
        return result;
    }


    public <T> long sdiffstore(String destKey, String... keys) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
        list.remove(0);
        return Optional.ofNullable(redisTemplate.opsForSet().differenceAndStore(keys[0], list, destKey)).orElse(0L);
    }


    public <T> Set<T> sinter(Class<T> clazz, String... keys) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
        list.remove(0);
        Set<Object> values = redisTemplate.opsForSet().intersect(keys[0], list);
        if (values == null) return null;
        Set<T> result = new HashSet<>();
        values.forEach(e -> {
            result.add(Jackson2JsonRedisSerializer.deserialize(e, clazz));
        });
        return result;
    }


    public <T> long sinterstore(String destKey, String... keys) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
        list.remove(0);
        return Optional.ofNullable(redisTemplate.opsForSet().intersectAndStore(keys[0], list, destKey)).orElse(0L);
    }


    public <T> Set<T> sunion(Class<T> clazz, String... keys) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
        list.remove(0);
        Set<Object> values = redisTemplate.opsForSet().union(keys[0], list);
        if (values == null) return null;
        Set<T> result = new HashSet<>();
        values.forEach(e -> {
            result.add(Jackson2JsonRedisSerializer.deserialize(e, clazz));
        });
        return result;
    }


    public <T> long sunionstore(String destKey, String... keys) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
        list.remove(0);
        return Optional.ofNullable(redisTemplate.opsForSet().unionAndStore(keys[0], list, destKey)).orElse(0L);
    }


    public <T> boolean sismember(String key, T member) {
        return Optional.ofNullable(redisTemplate.opsForSet().isMember(key, member)).orElse(Boolean.FALSE);
    }


    public <T> Set<T> smembers(String key, Class<T> clazz) {
        Set<Object> values = redisTemplate.opsForSet().members(key);
        if (values == null) return null;
        Set<T> result = new HashSet<>();
        values.forEach(e -> result.add(Jackson2JsonRedisSerializer.deserialize(e, clazz)));
        return result;
    }


    public <T> long srem(String key, T... members) {
        return Optional.ofNullable(redisTemplate.opsForSet().remove(key, members)).orElse(0L);
    }


    public <T> long sadd(String key, T... members) {
        return Optional.ofNullable(redisTemplate.opsForSet().add(key, members)).orElse(0L);

    }


    public <T> long scard(String key) {
        return Optional.ofNullable(redisTemplate.opsForSet().size(key)).orElse(0L);
    }

    // ===============================list=================================


    public <T> T lindex(String key, int index, Class<T> clazz) {
        Object value = redisTemplate.opsForList().index(key, index);
        return Jackson2JsonRedisSerializer.deserialize(value, clazz);
    }

    public <T> T lpop(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForList().leftPop(key);
        if (value == null) return null;
        return Jackson2JsonRedisSerializer.deserialize(value, clazz);
    }


    public <T> long lpush(String key, T... values) {
        return Optional.ofNullable(redisTemplate.opsForList().leftPushAll(key, values)).orElse(0L);
    }


    public <T> List<T> lrange(String key, int start, int stop, Class<T> clazz) {
        List<Object> values = redisTemplate.opsForList().range(key, start, start);
        if (values == null) return null;
        List<T> result = new ArrayList<>();
        values.forEach(e -> {
            result.add(Jackson2JsonRedisSerializer.deserialize(e, clazz));
        });
        return result;
    }


    public <T> void lset(String key, int index, T value) {
        redisTemplate.opsForList().set(key, index, value);
    }


    public void ltrim(String key, int start, int stop) {
        redisTemplate.opsForList().trim(key, start, stop);
    }


    public <T> T rpop(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForList().rightPop(key);
        return Jackson2JsonRedisSerializer.deserialize(value, clazz);
    }


    public <T> long rpush(String key, T... values) {
        return Optional.ofNullable(redisTemplate.opsForList().rightPushAll(key, values)).orElse(0L);
    }


    public long llen(String key) {
        return Optional.ofNullable(redisTemplate.opsForList().size(key)).orElse(0L);
    }


    public Object eval(String script) {
        // 执行 lua 脚本
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);
        return redisTemplate.execute(redisScript, Collections.emptyList());
    }


    public Object eval(String script, int keyCount, String... params) {
        if (keyCount > params.length) {
            throw new RedisException("keyCount > params");
        }

        // 执行 lua 脚本
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);
        List<String> keys = new ArrayList<>(keyCount);
        keys.addAll(Arrays.asList(params).subList(0, keyCount));
        List<String> args = new ArrayList<>(Arrays.asList(params).subList(keyCount, params.length));
        return redisTemplate.execute(redisScript, keys, args);
    }


    public Object eval(String script, List<String> keys, List<String> args) {
        // 执行 lua 脚本
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);
        return redisTemplate.execute(redisScript, keys, args);
    }

    public static class Jackson2JsonRedisSerializer<T> implements RedisSerializer<T> {
        public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        private final Class<T> clazz;

        public Jackson2JsonRedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            String s = JsonUtils.toJson(t);
            return s.getBytes(DEFAULT_CHARSET);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length <= 0) {
                return null;
            }
            String str = new String(bytes, DEFAULT_CHARSET);

            return JsonUtils.parse(str, clazz);
        }

        public static <T> T deserialize(Object value, Class<T> clazz) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return JsonUtils.parse(value.toString(), clazz);
            }
            String strValue = JsonUtils.toJson(value);
            return JsonUtils.parse(strValue, clazz);
        }

        public static <T> T deserialize(Object value, TypeReference<T> type) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return JsonUtils.parse(value.toString(), type);
            }
            String strValue = JsonUtils.toJson(value);
            return JsonUtils.parse(strValue, type);
        }
    }
}