package com.dk.study.common.domain;

import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author dk
 * @date 2022/08/25 15:50
 **/
@NoArgsConstructor
public class ResultVo extends HashMap<String, Object> implements Serializable {

    private ResultVo(int code, String msg, @Nullable Object data) {
        super.put("code", code);
        super.put("msg", msg);
        if (data != null) {
            super.put("data", data);
        }
    }

    public static ResultVo success() {
        return success("操作成功");
    }

    public static ResultVo success(Object data) {
        return success("操作成功", data);
    }

    public static ResultVo success(String msg) {
        return success(msg, null);
    }

    public static ResultVo success(String msg, Object data) {
        return new ResultVo(200, msg, data);
    }

    public static ResultVo error() {
        return error("操作失败");
    }

    public static ResultVo error(String msg) {
        return error(msg, null);
    }

    public static ResultVo error(String msg, Object data) {
        return new ResultVo(500, msg, data);
    }

    public static ResultVo error(int code, String msg) {
        return new ResultVo(code, msg, null);
    }
}
