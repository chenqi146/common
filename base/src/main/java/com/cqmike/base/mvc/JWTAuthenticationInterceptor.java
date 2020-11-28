package com.cqmike.base.mvc;

import cn.hutool.core.util.StrUtil;
import com.cqmike.base.auth.Auth;
import com.cqmike.base.exception.BusinessException;
import com.cqmike.base.exception.CommonEnum;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @program: common
 * @description: jwt
 * @author: chen qi
 * @create: 2020-11-28 11:23
 **/
public abstract class JWTAuthenticationInterceptor implements HandlerInterceptor {

    protected StringRedisTemplate redisTemplate;

    public JWTAuthenticationInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final static String TOKEN_HEADER = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader(TOKEN_HEADER);
        if (StrUtil.isEmpty(token) || !StrUtil.startWith(token, TOKEN_PREFIX)) {
            throw new BusinessException(CommonEnum.FORBIDDEN);
        }

        if (StrUtil.startWith(token, TOKEN_PREFIX)) {
            token = StrUtil.removePrefix(token, TOKEN_PREFIX);
        }

        String key = TOKEN_HEADER + StrUtil.COLON + token;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey == null || !hasKey) {
            throw new BusinessException(CommonEnum.SIGNATURE_NOT_MATCH);
        }

        String json = redisTemplate.opsForValue().get(key);
        afterValid(json);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return true;
    }

    protected void afterValid(String redisJson) {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Auth.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}