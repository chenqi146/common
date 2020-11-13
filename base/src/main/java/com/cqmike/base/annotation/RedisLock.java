package com.cqmike.base.annotation;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 *  分布式锁注解
 * @author chenqi
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedisLock {

    /**
     *  锁的key 后缀
     * @see RedisLock#keyPrefix()
     * <p>key的生成方式支持spel表达式</p>
     *  <code>
     *      redisKey = keyPrefix + ":" + key;
     *  </code>
     * @return key
     */
    String key();

    /**
     *  锁的前缀
     * {@value} 默认LOCK
     * @see RedisLock#key()
     * <p>keyPrefix的生成方式支持spel表达式</p>
     * @return keyPrefix
     */
    String keyPrefix() default "LOCK";

    /**
        过期时间  默认5秒
       @see RedisLock#timeUnit() 单位
      */
    int expire() default 5;

    /**
     * 获取锁的等待时间  默认1秒
     * 单位为毫秒
     * @return
     */
    long waitTime() default 1000;

    /**
     *  锁的过期时间 单位  默认秒
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
