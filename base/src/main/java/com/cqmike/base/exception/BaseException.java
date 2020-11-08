package com.cqmike.base.exception;

/**
 * @program: iot
 * @ClassName: BaseException
 * @Description: 基础异常类
 * @Author: chen qi
 * @Date: 2020/2/23 11:14
 * @Version: 1.0
 **/
public abstract class BaseException extends RuntimeException {


    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ";";
    }

    public abstract Integer getErrorCode();
    public abstract String getErrorMsg();
}
