package com.work.integratedDesign.exception;

import com.work.integratedDesign.pojo.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


// 全局异常处理
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResult<String> exceptionHandler(Exception e) {
        log.error("异常:{}", e.getMessage());
        return JsonResult.error(e.getMessage());
    }
}

