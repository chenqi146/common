package com.cqmike.base.mvc;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * @program: common
 * @description: 返回包装拦截器
 * @author: chen qi
 * @create: 2020-11-08 10:07
 **/
@Component
public class ResponseInterceptor implements HandlerInterceptor {

    public static final String RESPONSE_RESULT_ANN = "RESPONSE-RESULT-ANN";

    private static final Set<Class<?>> SET = Sets.newConcurrentHashSet();
    private static final Set<Method> METHOD_SET = Sets.newConcurrentHashSet();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> beanType = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();

            if (SET.contains(beanType)) {
                request.setAttribute(RESPONSE_RESULT_ANN, beanType.getAnnotation(RestResponse.class));
            } else {
                request.setAttribute(RESPONSE_RESULT_ANN, beanType.getAnnotation(RestResponse.class));
                SET.add(beanType);
            }

            if (METHOD_SET.contains(method)) {
                request.setAttribute(RESPONSE_RESULT_ANN, method.getAnnotation(RestResponse.class));
            } else if (method.isAnnotationPresent(RestResponse.class)) {
                request.setAttribute(RESPONSE_RESULT_ANN, method.getAnnotation(RestResponse.class));
                METHOD_SET.add(method);
            }
        }
        return true;
    }
}
