package com.cqmike.core.annotation;

import com.cqmike.core.util.JsonUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: iot
 * @ClassName: SearchFormAspect
 * @Description: 切面测试
 * @Author: chen qi
 * @Date: 2020/2/20 22:01
 * @Version: 1.0
 **/
@Aspect
@Component
public class WebMvcAspect {

    private static final Logger log = LoggerFactory.getLogger(WebMvcAspect.class);

    @Pointcut("execution(public * com.cqmike..*.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 在切点之前织入
     * @param joinPoint
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 打印请求相关参数
        log.debug("========================================== Start ==========================================");
        // 打印请求 url
        log.debug("URL            : {}", request.getRequestURL().toString());
        // 打印 Http method
        log.debug("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.debug("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        log.debug("IP             : {}", request.getRemoteAddr());
        // 打印请求入参
        log.debug("Request Args   : {}", JsonUtils.toJson(joinPoint.getArgs()));
    }

    /**
     * 在切点之后织入
     */
    @After("webLog()")
    public void doAfter() {
        log.debug("=========================================== End ===========================================");
        // 每个请求之间空一行
        log.debug("");
    }

    /**
     * 环绕
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        log.debug("Response Args  : {}", JsonUtils.toJson(result));
        // 执行耗时
        log.debug("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }


}
