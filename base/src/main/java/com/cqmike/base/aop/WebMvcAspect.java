package com.cqmike.base.aop;

import com.cqmike.base.util.JsonUtils;
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
     * 环绕
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("webLog()中ServletRequestAttributes为空");
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        // 打印请求相关参数
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        Log l = new Log();
        l.setIp(request.getRemoteAddr());
        l.setUrl(request.getRequestURL().toString());
        l.setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()));
        l.setHttpMethod(request.getMethod());
        l.setRequestParams(JsonUtils.toJson(joinPoint.getArgs()));
        l.setResult(JsonUtils.toJson(result));
        l.setTimeCost(System.currentTimeMillis() - startTime);
        log.debug("Request Log Info     : {}", JsonUtils.toJson(l));
        // 每个请求之间空一行
        return result;
    }

    static class Log {
        private String ip;
        private String url;
        private String httpMethod;
        private String classMethod;
        private Object requestParams;
        private Object result;
        private Long timeCost;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        public String getClassMethod() {
            return classMethod;
        }

        public void setClassMethod(String classMethod) {
            this.classMethod = classMethod;
        }

        public Object getRequestParams() {
            return requestParams;
        }

        public void setRequestParams(Object requestParams) {
            this.requestParams = requestParams;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public Long getTimeCost() {
            return timeCost;
        }

        public void setTimeCost(Long timeCost) {
            this.timeCost = timeCost;
        }
    }


}
