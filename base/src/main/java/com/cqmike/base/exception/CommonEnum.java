package com.cqmike.base.exception;

/**
 * @program: iot
 * @EnumName: CommonEnum
 * @Description: CommonEnum
 * @Author: chen qi
 * @Date: 2019/12/22 19:23
 * @Version: 1.0
 **/

public enum CommonEnum implements BaseErrorInfoInterface {
    SUCCESS(200, "成功!"),
    BODY_NOT_MATCH(400, "请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH(401, "请求的数字签名不匹配!"),
    FORBIDDEN(403, "没有对应的权限!"),
    NOT_FOUND(404, "未找到该资源!"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误!"),
    SERVER_BUSY(503, "服务器正忙，请稍后再试!");

    private final Integer resultCode;
    private final String resultMsg;

    private CommonEnum(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public Integer getResultCode() {
        return this.resultCode;
    }

    public String getResultMsg() {
        return this.resultMsg;
    }
}