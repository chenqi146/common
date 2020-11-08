package com.cqmike.base.exception;


import org.springframework.http.HttpStatus;

public class ApiAuthorityException extends BaseException {
    private static final long serialVersionUID = 1L;
    protected Integer errorCode;
    protected String errorMsg;

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ApiAuthorityException() {
        this.errorCode = CommonEnum.FORBIDDEN.getResultCode();
        this.errorMsg = CommonEnum.FORBIDDEN.getResultMsg();
    }

    public ApiAuthorityException(String errorMsg) {
        super(errorMsg);
        this.errorCode = HttpStatus.FORBIDDEN.value();
        this.errorMsg = errorMsg;
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
