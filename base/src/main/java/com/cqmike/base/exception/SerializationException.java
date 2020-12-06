package com.cqmike.base.exception;

/**
 * @program: common
 * @description:
 * @author: chen qi
 * @create: 2020-12-06 10:15
 **/
public class SerializationException extends BaseException {

    public SerializationException() {
    }

    public SerializationException(String message) {
        super(message);
        this.errorCode = CommonEnum.SERIALIZER_ERROR.getResultCode();
        this.errorMsg = CommonEnum.SERIALIZER_ERROR.getResultMsg();
    }

    public SerializationException(String template, Object... messages) {
        super(template, messages);

        this.errorCode = CommonEnum.SERIALIZER_ERROR.getResultCode();
        this.errorMsg = CommonEnum.SERIALIZER_ERROR.getResultMsg();
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = CommonEnum.SERIALIZER_ERROR.getResultCode();
        this.errorMsg = CommonEnum.SERIALIZER_ERROR.getResultMsg();
    }

    public SerializationException(Throwable cause) {
        super(cause);
        this.errorCode = CommonEnum.SERIALIZER_ERROR.getResultCode();
        this.errorMsg = CommonEnum.SERIALIZER_ERROR.getResultMsg();
    }

}
