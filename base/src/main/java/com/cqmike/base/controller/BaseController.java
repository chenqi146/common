package com.cqmike.base.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @program: iot
 * @Description 基类 Controller
 * @Author 陈琪
 * @Date 2020-2-16 0016 22:24
 * @Version 1.0
 **/
public class BaseController {

    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";


    public BaseController() {
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        DateFormat format = new SimpleDateFormat(pattern);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }

}
