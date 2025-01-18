package com.lianshidai.bcebe.Controller;

import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.RegisterGet;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Service.Impl.VerificationEmailImpl;
import com.lianshidai.bcebe.Service.UserService;
import com.lianshidai.bcebe.Utils.EncryptPassword;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// 注册
@RestController
public class Register {
    @Resource
    private UserService userService;
    @Resource
    private VerificationEmailImpl verificationEmail;
    // 注册
    @PostMapping(value = "/register")
    public JsonResult<Integer> register(@Validated @NotNull(message = "信息不能为空")  @RequestBody RegisterGet registerGet){
        String code = registerGet.getCode();
        User user= registerGet.getUser();
        if(!verificationEmail.CodeVerification(code, user.getEmail())){
            return new JsonResult<>(400,"验证码错误");
        }
        if(userService.lambdaQuery().eq(User::getStudentID,user.getStudentID()).one()!=null){
            return new JsonResult<>(400,"该学号已注册");
        }
        if(userService.lambdaQuery().eq(User::getEmail,user.getEmail()).one()!=null){
            return new JsonResult<>(400,"该邮箱已注册");
        }
        user.setPassword(EncryptPassword.encryptPassword(user.getPassword()));
        user.setStatus(0);
        if (userService.save(user)) {
            return new JsonResult<>(200, "注册成功");
        } else {
            return new JsonResult<>(500, "注册失败");
        }
    }
    // 发送验证码
    @PostMapping(value = "/register/email")
    public JsonResult<String> email(@RequestParam("to") String to){
        if(userService.lambdaQuery().eq(User::getEmail,to).one()!=null){
            return new JsonResult<>(400,"该邮箱已注册");
        }
        if(verificationEmail.EmailSend(to)){
            return new JsonResult<>(200,"发送成功");
        }
        return new JsonResult<>(400,"发送失败");
    }
}
