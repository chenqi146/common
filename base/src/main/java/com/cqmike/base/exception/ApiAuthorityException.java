package com.cqmike.base.exception;


import com.google.common.base.Strings;
import org.springframework.http.HttpStatus;

public class ApiAuthorityException extends BaseException {
    private static final long serialVersionUID = 1L;

    public ApiAuthorityException() {
        this.errorCode = CommonEnum.FORBIDDEN.getResultCode();
        this.errorMsg = CommonEnum.FORBIDDEN.getResultMsg();
    }

    public ApiAuthorityException(String template, Object... messages) {
        super(Strings.lenientFormat(template, messages));
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
