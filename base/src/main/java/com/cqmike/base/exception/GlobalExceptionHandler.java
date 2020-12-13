package com.cqmike.base.exception;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.List;

import com.cqmike.base.form.ReturnForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({BusinessException.class})
    @ResponseBody
    public ReturnForm<String> businessExceptionHandler(BusinessException e) {
        logger.error("业务异常！原因是：", e);
        return ReturnForm.error(e.getErrorCode(), e.getErrorMsg());
    }

    @ResponseBody
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class, ValidationException.class})
    public ReturnForm<String> validationExceptionHandler(Exception exception) {
        BindingResult bindResult = null;
        if (exception instanceof BindException) {
            bindResult = ((BindException)exception).getBindingResult();
        } else if (exception instanceof MethodArgumentNotValidException) {
            bindResult = ((MethodArgumentNotValidException)exception).getBindingResult();
        } else if (exception instanceof ValidationException) {
            ValidationException validationException = (ValidationException) exception;
            return ReturnForm.error(validationException.getMessage());
        }

        if (bindResult == null) {
            return ReturnForm.error(CommonEnum.SERVER_BUSY);
        } else {
            List<ObjectError> allErrors = bindResult.getAllErrors();
            if (CollectionUtils.isEmpty(allErrors)) {
                return ReturnForm.error(CommonEnum.SERVER_BUSY);
            } else {
                StringBuilder msg = new StringBuilder();

                for (ObjectError allError : allErrors) {
                    msg.append(allError.getDefaultMessage()).append(" , ");
                }

                return ReturnForm.error(msg.toString());
            }
        }
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ReturnForm<String> exceptionHandler(NullPointerException e) {
        logger.error("空指针异常！原因是:", e);
        return ReturnForm.error(CommonEnum.BODY_NOT_MATCH);
    }

    /**
     *  暂未使用
     * @param e
     * @return
     */
    @ExceptionHandler({ApiAuthorityException.class})
    @ResponseBody
    public ReturnForm<String> exceptionHandler(ApiAuthorityException e) {
        logger.warn("权限认证异常！原因是: ", e);
        return ReturnForm.error(CommonEnum.FORBIDDEN);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ReturnForm<String> exceptionHandler(Exception e) {
        logger.error("服务器异常！原因是：", e);
        return ReturnForm.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }
}
