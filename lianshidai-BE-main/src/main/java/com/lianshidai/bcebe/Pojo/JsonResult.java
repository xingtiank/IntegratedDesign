package com.lianshidai.bcebe.Pojo;

import lombok.Getter;
import lombok.Setter;


// 封装返回结果
@Setter
@Getter
public class JsonResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public JsonResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public JsonResult() {
        this(200, "success", null);
    }

    public JsonResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JsonResult(T data) {
        this(200, "success", data);
    }

    public JsonResult(T data,String msg) {
        this(200, msg, data);
    }
}
