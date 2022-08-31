package com.dk.study.common.domain.enums;

/**
 * @author dk
 * @date 2022/08/29 11:36
 **/
public enum ResultCode {

    /**
     * 异常
     */
    VALID_ERROR(10010,"参数校验失败"),
    INTERNAL_ERROR(500,"系统内部错误");

    private final Integer code;

    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
