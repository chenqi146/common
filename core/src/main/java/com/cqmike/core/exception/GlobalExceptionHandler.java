package com.cqmike.core.exception;

import com.cqmike.core.form.ResultBody;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: iot
 * @ClassName: GlobalExceptionHandler
 * @Description: GlobalExceptionHandler
 * @Author: chen qi
 * @Date: 2019/12/22 19:27
 * @Version: 1.0
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ResultBody businessExceptionHandler(BusinessException e) {
        logger.error("业务异常！原因是：{}", e.getMessage());
        return ResultBody.error(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 所有验证框架异常捕获处理
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public ResultBody validationExceptionHandler(Exception exception) {
        BindingResult bindResult = null;
        if (exception instanceof BindException) {
            bindResult = ((BindException) exception).getBindingResult();
        } else if (exception instanceof MethodArgumentNotValidException) {
            bindResult = ((MethodArgumentNotValidException) exception).getBindingResult();
        }

        if (bindResult == null) {
            return ResultBody.error(CommonEnum.SERVER_BUSY);
        }
        List<ObjectError> allErrors = bindResult.getAllErrors();
        if (CollectionUtils.isEmpty(allErrors)) {
            return ResultBody.error(CommonEnum.SERVER_BUSY);
        }

        StringBuilder msg = new StringBuilder();
        for (ObjectError allError : allErrors) {
            msg.append(allError.getDefaultMessage()).append(" , ");
        }

        return ResultBody.error(msg.toString());
    }

    /**
     * 处理自定义的业务异常
     *
     * @param e
     * @returng
     */
    @ExceptionHandler(value = GeneratePojoConvertException.class)
    @ResponseBody
    public ResultBody generatePojoConvertExceptionHandler(GeneratePojoConvertException e) {
        logger.error("{}！原因是：{}", e.getErrorMsg(), e.getMessage());
        return ResultBody.error(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultBody exceptionHandler(NullPointerException e) {
        logger.error("空指针异常！原因是: {}", e.getMessage());
        return ResultBody.error(CommonEnum.BODY_NOT_MATCH);
    }


    /**
     * 处理其他异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest req, Exception e) {
        logger.error("未知异常！原因是: {}", e.getMessage());
        return ResultBody.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }

}
