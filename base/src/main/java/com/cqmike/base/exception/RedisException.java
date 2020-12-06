package com.cqmike.base.exception;

/**
 * @program: common
 * @description:
 * @author: chen qi
 * @create: 2020-12-06 10:13
 **/
public class RedisException extends BaseException {

    public RedisException() {

        this.errorCode = CommonEnum.REDIS_ERROR.getResultCode();
        this.errorMsg = CommonEnum.REDIS_ERROR.getResultMsg();
    }

    public RedisException(String message) {
        super(message);
        this.errorCode = CommonEnum.REDIS_ERROR.getResultCode();
        this.errorMsg = CommonEnum.REDIS_ERROR.getResultMsg();
    }

    public RedisException(String template, Object... messages) {
        super(template, messages);

        this.errorCode = CommonEnum.REDIS_ERROR.getResultCode();
        this.errorMsg = CommonEnum.REDIS_ERROR.getResultMsg();
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = CommonEnum.REDIS_ERROR.getResultCode();
        this.errorMsg = CommonEnum.REDIS_ERROR.getResultMsg();
    }

    public RedisException(Throwable cause) {
        super(cause);
        this.errorCode = CommonEnum.REDIS_ERROR.getResultCode();
        this.errorMsg = CommonEnum.REDIS_ERROR.getResultMsg();
    }

}
