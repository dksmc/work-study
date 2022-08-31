package com.dk.study.core.temp.domain;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author dk
 * @date 2022/08/29 13:51
 **/
@Data
public class TempParam {

    @NotBlank(message = "请求参数缺失(esId)")
    private String esId;

    private String supType;

    @Valid
    @NotEmpty(message = "集合不能为空")
    private List<BaseInfo> baseInfoList;
}
