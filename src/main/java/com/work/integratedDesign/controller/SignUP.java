package com.work.integratedDesign.controller;

import com.work.integratedDesign.pojo.JsonResult;
import com.work.integratedDesign.pojo.User;
import com.work.integratedDesign.service.EmailSendingService;
import com.work.integratedDesign.service.UserService;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;



//注册
@RestController
public class SignUP {
    @Resource
    private UserService userService;
    @Resource
    private EmailSendingService emailSendingService;
    @PostMapping("/signUp")
    public  JsonResult<String> signUp(@NotNull String username,@NotNull String password,@NotNull String email,@Nullable Integer code)
    {
        userService.newUserSave(new User(username,password,email));
        return JsonResult.success("注册成功");
//        if (emailSendingService.CodeVerification(code.toString(),email))
//        {
//            userService.newUserSave(new User(username,password,email));
//            return JsonResult.success("注册成功");
//        }
//        else {
//            return JsonResult.error("验证码错误");
//        }
    }
    @PostMapping("/PasswordReset")
    public  JsonResult<String> PasswordReset(@NotNull String password,@NotNull String email,@NotNull Integer code)
    {
        if (emailSendingService.CodeVerification(code.toString(),email))
        {
            userService.passwordReset(email,password);
            return JsonResult.success("密码重置成功");
        }
        else {
            return JsonResult.error("验证码错误");
        }
    }
}
