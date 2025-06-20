package com.work.integratedDesign.pojo;

import lombok.Getter;
import lombok.Setter;

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

    public JsonResult(String msg,T data) {
        this(200, msg, data);
    }

    public static JsonResult<String> error(String message) {
        return new JsonResult<>(400, message);
    }
    public static JsonResult<String> success(String message) {
        return new JsonResult<>(200, message);
    }
}
