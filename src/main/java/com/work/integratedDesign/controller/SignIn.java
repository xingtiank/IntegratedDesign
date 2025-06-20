package com.work.integratedDesign.controller;

import com.work.integratedDesign.pojo.JsonResult;
import com.work.integratedDesign.service.UserService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import java.security.NoSuchAlgorithmException;

//登录
@RestController
public class SignIn {
    @Resource
    private UserService userService;
    @PostMapping("/signIn")
    public  JsonResult<String> signIn(@NotNull String email, @NotNull String password) throws NoSuchAlgorithmException {
        if (!userService.exists(email,password))
            return JsonResult.error("用户名或密码错误");
        return JsonResult.success("登录成功");
    }
}
