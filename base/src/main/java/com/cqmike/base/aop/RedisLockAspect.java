package com.cqmike.base.aop;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.cqmike.base.annotation.RedisLock;
import com.cqmike.base.generator.SnowflakeIdWorker;
import com.cqmike.base.util.RedisLockUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁 切面处理类
 *
 * @author chen qi
 * @date 2020-10-29 18:25
 **/
@Aspect
@Component
public class RedisLockAspect {

    private static final Logger log = LoggerFactory.getLogger(RedisLockAspect.class);

    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;

    private StringRedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(com.cqmike.base.annotation.RedisLock)")
    public Object aroundCache(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        final RedisLock annotation = methodSignature.getMethod().getAnnotation(RedisLock.class);

        final String keyPrefix = getRealForSpELKey(joinPoint, annotation.keyPrefix());
        final String key = getRealForSpELKey(joinPoint, annotation.key());
        String lockKey = keyPrefix + StrUtil.COLON + key;

        String value = snowflakeIdWorker.getId().toString();

        // 获取不到锁 跳过此执行方法
        int expire = annotation.expire();
        final TimeUnit timeUnit = annotation.timeUnit();
        if (timeUnit != TimeUnit.SECONDS) {
            expire = (int) timeUnit.toSeconds(expire);
        }

        final String format = StrUtil.format("线程id: {}, 类: {}, 方法: {}", Thread.currentThread().getId(),
                joinPoint.getTarget().getClass().getSimpleName(), methodSignature.getMethod().getName());
        // 尝试获取分布式锁  根据等待时间去重试获取锁
        try {
            if (!RedisLockUtil.lockWithWaitTime(redisTemplate, lockKey, value, expire, annotation.waitTime())) {
                return null;
            }
        } catch (Exception e) {
            log.error("{}, 尝试获取分布式锁异常", format, e);
            if (!RedisLockUtil.releaseDistributedLock(redisTemplate, lockKey, value)) {
                log.error("{}, 尝试获取分布式锁异常时分布式锁释放失败, key: {}, value: {}", format, lockKey, value);
            } else {
                log.info("{}, 尝试获取分布式锁异常时分布式锁释放成功, key: {}, value: {}", format, lockKey, value);
            }
            return null;
        }

        try {

            log.info("{} ,获取到分布式锁: key: {}, value: {}", format, lockKey, value);
            LockExpandDaemonRunnable runnable = new LockExpandDaemonRunnable(redisTemplate, lockKey, value, expire);
            // 开启redis 过期时间 续期守护线程
            Thread thread = new Thread(runnable);
            thread.setDaemon(Boolean.TRUE);
            thread.start();

            Object proceed = null;
            try {
                proceed = joinPoint.proceed();
            } catch (Throwable throwable) {
                log.error("{}, 分布式锁方法执行异常", format, throwable);
            }
            runnable.stop();
            thread.interrupt();

            return proceed;
        } finally {
            if (!RedisLockUtil.releaseDistributedLock(redisTemplate, lockKey, value)) {
                log.error("{}, 分布式锁释放失败, key: {}, value: {}", format, lockKey, value);
            } else {
                log.info("{}, 分布式锁释放成功, key: {}, value: {}", format, lockKey, value);
            }

        }

    }

    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final String SPEL_FLAG = "#";

    private String getRealForSpELKey(ProceedingJoinPoint joinPoint, String key) {
        return key.contains(SPEL_FLAG) ? this.generateKeyBySpEL(key, joinPoint) : key;
    }

    /**
     * 获取spel表达式的值
     *
     * @param spELString spel表达式
     * @param joinPoint  切入点
     * @return
     */
    private String generateKeyBySpEL(String spELString, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (ArrayUtil.isEmpty(paramNames)) {
            return spELString;
        }
        Expression expression = parser.parseExpression(spELString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {

            context.setVariable(paramNames[i], args[i]);
        }
        final Object value = expression.getValue(context);
        if (value == null) {
            return spELString;
        }
        return value.toString();
    }

    public static class LockExpandDaemonRunnable implements Runnable {

        private static final Logger log = LoggerFactory.getLogger(LockExpandDaemonRunnable.class);

        private final StringRedisTemplate template;
        private final String key;
        private final String value;
        private final int lockTime;

        public LockExpandDaemonRunnable(StringRedisTemplate template, String key, String value, int lockTime) {
            this.template = template;
            this.key = key;
            this.value = value;
            this.lockTime = lockTime;
            this.signal = Boolean.TRUE;
        }

        private volatile Boolean signal;

        public void stop() {
            this.signal = Boolean.FALSE;
        }

        @Override
        public void run() {

            // 先等待锁的过期时间的三分之二  如果还持有锁 进行一次续期 重复
            int waitTime = lockTime * 1000 * 2 / 3;
            while (signal) {
                try {
                    Thread.sleep(waitTime);
                    if (RedisLockUtil.expandLockTime(template, key, value, lockTime)) {
                        // 延长过期时间成功
                        log.debug("延长过期时间成功，本次等待{}ms，将重置key为{}的锁超时时间重置为{}s", waitTime, key, lockTime);
                    } else {
                        log.debug("延长过期时间失败, 此线程LockExpandDaemonRunnable中断");
                        this.stop();
                    }
                } catch (InterruptedException e) {
                    log.debug("此线程LockExpandDaemonRunnable被强制中断", e);
                } catch (Exception e) {
                    log.error("锁的延长时间守护线程发送异常", e);
                }
            }

        }
    }

}
