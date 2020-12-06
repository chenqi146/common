package com.cqmike.base.exception;

import com.google.common.base.Strings;
import org.springframework.http.HttpStatus;

/**
 * @program: iot
 * @ClassName: BusinessException
 * @Description: BusinessException  业务异常类
 * @Author: chen qi
 * @Date: 2019/12/22 19:26
 * @Version: 1.0
 **/
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    @Override
    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public BusinessException() {
        super();
    }

    public BusinessException(String template, Object... messages) {
        super(Strings.lenientFormat(template, messages));
    }

    public BusinessException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultMsg());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    public BusinessException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getResultMsg(), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    public BusinessException(String errorMsg) {
        super(errorMsg);
        this.errorCode = HttpStatus.BAD_REQUEST.value();
        this.errorMsg = errorMsg;
    }

    public BusinessException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}