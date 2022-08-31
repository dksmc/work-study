package com.dk.study.core.temp.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dk
 * @date 2022/08/29 13:52
 **/
@Data
public class BaseInfo {

    @NotBlank(message = "请求参数缺失(id)！")
    private String id;
}
