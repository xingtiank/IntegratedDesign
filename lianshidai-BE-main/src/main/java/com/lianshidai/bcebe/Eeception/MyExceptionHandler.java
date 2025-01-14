package com.lianshidai.bcebe.Eeception;

import com.lianshidai.bcebe.Pojo.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//全局异常捕获
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {

    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public JsonResult<String> exceptionHandler(Exception e){
        log.info("异常:{}", e.getMessage());
        return new JsonResult<>(400,e.getMessage());
    }

}

