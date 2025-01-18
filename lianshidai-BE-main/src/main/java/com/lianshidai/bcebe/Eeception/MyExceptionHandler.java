package com.lianshidai.bcebe.Eeception;

import com.lianshidai.bcebe.Pojo.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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


    @ExceptionHandler(value = ArticleException.class)
    @ResponseBody
    public JsonResult<String> myExceptionHandler(ArticleException e){
        log.error("uuy的全局异常处理输出:{}", e.getMessage());
        return new JsonResult<>(400,e.getMessage());
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    @ResponseBody
    public JsonResult<String> myResourceFoundExceptionHandler(NoResourceFoundException e){
        log.error("资源获取异常 {}", e.getMessage());
        return new JsonResult<>(400,e.getMessage());
    }
    //http异常处理
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return new ResponseEntity<>("No acceptable representation", HttpStatus.NOT_ACCEPTABLE);
    }

}

