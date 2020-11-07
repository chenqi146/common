package com.cqmike.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: iot
 * @ClassName: GeneratePojoConvertException
 * @Description: 动态生成实体类异常类
 * @Author: chen qi
 * @Date: 2020/2/23 11:11
 * @Version: 1.0
 **/
public class GeneratePojoConvertException extends BaseException {

    public GeneratePojoConvertException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Override
    public String getErrorMsg() {
        return "动态生成实体类异常";
    }
}
