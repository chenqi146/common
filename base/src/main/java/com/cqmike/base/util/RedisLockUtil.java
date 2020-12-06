package com.cqmike.base.util;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用于Redis 分布式锁
 */
public final class RedisLockUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisLockUtil.class);

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    public static final Long SUCCESS = 1L;

    private final static String LOCK_LUA = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
            "redis.call('expire', KEYS[1], ARGV[2]) return 1 else return 0 end";

    private final static String LOCK_RELEASE_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 锁的过期时间延长脚本
     */
    private final static String LOCK_EXPAND_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "redis.call('expire', KEYS[1], ARGV[2]) return 1 else return 0 end";


    /**
     * 获取分布式锁
     *
     * @param lockKey    锁的key
     * @param value      当前锁的值
     * @param expireTime 过期时间
     * @return
     */
    public static boolean tryGetDistributedLock(RedisClient redisClient, String lockKey, String value, int expireTime) {

        List<String> values = Lists.newArrayList(value, String.valueOf(expireTime));

        Object result = redisClient.eval(LOCK_LUA, Collections.singletonList(lockKey), values);
        //判断是否成功
        return Objects.equals(SUCCESS, result);
    }

    /**
     * 释放分布式锁
     *
     * @param redisTemplate
     * @param lockKey 锁的key
     * @param value   当前锁的值
     * @return
     */
    public static boolean releaseDistributedLock(RedisClient redisClient, String lockKey, String value) {
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Object result = redisClient.eval(LOCK_RELEASE_LUA, Collections.singletonList(lockKey), Collections.singletonList(value));
        return Objects.equals(SUCCESS, result);
    }

    /**
     * 延长锁的超时时间
     *
     * @param lockKey  锁的key
     * @param value    当前锁的值
     * @param lockTime 锁的过期延长时间
     * @return
     */
    public static boolean expandLockTime(RedisClient redisClient, String lockKey, String value, int lockTime) {
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Object result = redisClient.eval(LOCK_EXPAND_LUA, Collections.singletonList(lockKey), Lists.newArrayList(value, String.valueOf(lockTime)));
        return Objects.equals(SUCCESS, result);
    }


    private static final long SLEEP_TIME = 100;

    /**
     * 自定义获取锁的超时时间
     *
     * @param lockKey             锁的key
     * @param value               当前锁的值
     * @param expireTime          过期时间  秒
     * @param waitMilliSecondTime 等待时间  毫秒
     * @return true -> 获取到锁
     * @throws InterruptedException
     */
    public static boolean lockWithWaitTime(RedisClient redisClient, String lockKey, String value, int expireTime, long waitMilliSecondTime) throws InterruptedException {
        while (waitMilliSecondTime >= 0) {
            final boolean lock = tryGetDistributedLock(redisClient, lockKey, value, expireTime);
            if (lock) {
                return true;
            }
            waitMilliSecondTime -= SLEEP_TIME;
            Thread.sleep(SLEEP_TIME);
        }
        return false;
    }


}
