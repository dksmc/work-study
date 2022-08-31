package com.dk.study.common.exception.custom;

/**
 * @author dk
 * @date 2022/08/29 11:43
 **/
public class BaseException extends RuntimeException{

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }
}
