package com.dk.study.common.exception.custom;

/**
 * @author dk
 * @date 2022/08/29 11:26
 **/
public class CustomException extends BaseException {

    public CustomException(String msg) {
        super(msg);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}
