package com.cqmike.base.form;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.cqmike.base.exception.BaseErrorInfoInterface;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
public class ReturnForm<T> implements Serializable {
    private boolean success;
    private int status;
    private T message;
    private String errorMessage;

    public ReturnForm() {
        this.status = -1;
    }

    public ReturnForm(int status, T message) {
        this(status == 200, status, message);
    }

    public ReturnForm(T message) {
        this(true, message);
    }

    public ReturnForm(boolean success) {
        this(success, null);
    }

    public ReturnForm(boolean success, T message) {
        this(success, success ? 200 : -1, message);
    }

    public ReturnForm(boolean success, int status, T message) {
        this.success = success;
        this.status = status;
        if (success) {
            this.message = message;
        } else if (message != null) {
            this.errorMessage = message.toString();
        }

    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ReturnForm<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public static <T> ReturnForm<T> success() {
        return new ReturnForm<>(true);
    }

    public static <T> ReturnForm<T> success(T t) {
        return new ReturnForm<>(t);
    }

    public static <T> ReturnForm<T> message(int status, T t) {
        return new ReturnForm<>(status, t);
    }

    public static <T> ReturnForm<T> error(T t) {
        return new ReturnForm<>(false, t);
    }

    public static <T> ReturnForm<T> error(int status, T t) {
        return new ReturnForm<>(status, t);
    }

    public static ReturnForm<String> error(BaseErrorInfoInterface errorInfo) {
        return new ReturnForm<>(errorInfo.getResultCode(), errorInfo.getResultMsg());
    }

    public T getMessage() {
        return this.message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public ReturnForm<T> message(T message) {
        this.message = message;
        return this;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ReturnForm<T> errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ReturnForm<T> status(int status) {
        this.status = status;
        return this;
    }
}
