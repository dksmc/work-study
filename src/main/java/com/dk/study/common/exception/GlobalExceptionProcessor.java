package com.dk.study.common.exception;

import com.dk.study.common.domain.enums.ResultCode;
import com.dk.study.common.exception.custom.CustomException;
import com.dk.study.common.validate.ValidateError;
import com.dk.study.common.domain.ResultVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dk
 * @date 2022/08/25 16:45
 **/
@Slf4j
@RestControllerAdvice(basePackages = {"com.dk.study"})
public class GlobalExceptionProcessor implements ResponseBodyAdvice<Object> {

    /**
     * 校验参数
     *
     * @param ex 异常
     * @return ResultVo
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResultVo handleValidateException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ValidateError> allErrors = bindingResult.getAllErrors().stream().map(ValidateError::new).collect(Collectors.toList());
        log.info(ex.getMessage());
        return ResultVo.error(ResultCode.VALID_ERROR.getMsg(), allErrors);
    }

    /**
     * 校验参数
     *
     * @param ex 异常
     * @return ResultVo
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResultVo handleValidateException(ConstraintViolationException ex) {
        List<ValidateError> collect = ex.getConstraintViolations().stream().map(ValidateError::new).collect(Collectors.toList());
        log.info(ex.getMessage());
        return ResultVo.error(ResultCode.VALID_ERROR.getMsg(), collect);
    }

    /**
     * 自定义异常
     *
     * @param ex 异常信息
     * @return resultVo
     */
    @ExceptionHandler({CustomException.class})
    public ResultVo handleBaseException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResultVo.error(ResultCode.INTERNAL_ERROR.getMsg(), ex.getMessage());
    }



    /**
     * ResponseBodyAdvice 是对 Controller 返回的内容在 HttpMessageConverter 进行类型转换之前拦截，
     * 进行相应的处理操作后，再将结果返回给客户端。那这样就可以把统一包装的工作放到这个类里面。
     * 判断是否要执行beforeBodyWrite方法，true为执行，false不执行
     * @param returnType 当前方法
     * @param converterType 类型
     * @return boolean
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class
            selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 可以对返回数据进行包装
        // 提供一定的灵活度，如果body已经被包装了，就不进行包装
        if (body instanceof ResultVo) {
            return body;
        }
        // 如果返回值是String类型，那就手动把Result对象转换成JSON字符串
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(ResultVo.success(body));
            } catch (JsonProcessingException e) {
                throw new CustomException(e);
            }
        }
        return ResultVo.success(body);
    }
}
