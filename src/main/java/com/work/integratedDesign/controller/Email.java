package com.work.integratedDesign.controller;

import com.work.integratedDesign.pojo.JsonResult;
import com.work.integratedDesign.service.EmailSendingService;
import com.work.integratedDesign.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

//发送验证码
@RestController
public class Email {
    @Resource
    private EmailSendingService emailSendingService;
    @Resource
    private UserService userService;
    //注册发送验证码
    @PostMapping("/emails-register")
    public JsonResult<String> sendRegisterEmail(String email){
        emailSendingService.VerificationCodeSending(email);
        return JsonResult.success("发送成功");
    }
    //找回密码发送验证码
    @PostMapping("/emails/reset-password")
    public JsonResult<String> sendFindPasswordEmail(String email){
        //验证用户是否存在
        if (userService.userExistsByEmail(email)) {
            emailSendingService.VerificationCodeSending(email);
            return JsonResult.success("发送成功");
        }
        return JsonResult.error("用户不存在");
    }
}
