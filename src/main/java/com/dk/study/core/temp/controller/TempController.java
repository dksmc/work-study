package com.dk.study.core.temp.controller;

import com.dk.study.core.temp.domain.TempParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @author dk
 * @date 2022/08/29 11:09
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/temp")
public class TempController {

    @GetMapping("/test1")
    public void test(@NotBlank(message = "请求参数缺失") String esId){
        log.info("esId:{}",esId);
    }

    @PostMapping("/testPost")
    public void testPost(@RequestBody @Validated TempParam param){
        log.info(param.toString());
    }
}
