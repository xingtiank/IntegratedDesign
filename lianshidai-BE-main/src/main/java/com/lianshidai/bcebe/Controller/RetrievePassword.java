package com.lianshidai.bcebe.Controller;

import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Service.Impl.VerificationEmailImpl;
import com.lianshidai.bcebe.Service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


//重置密码
@RestController
@Validated
public class RetrievePassword {
    @Resource
    private UserService userService;
    @Resource
    private VerificationEmailImpl verificationEmail;

    //重置密码
    @PostMapping(value = "/retrievePassword")
    @Transactional
    public JsonResult<String> retrievePassword(@Validated @NotBlank(message = "邮箱不能为空") String to,
                                               @Validated @NotBlank(message = "验证码不能为空") String code, String password)
    {
        if(password == null || password.length() < 10 || password.length() > 20){
            return new JsonResult<>(400,"密码长度在10-20之间");
        }
        if(!verificationEmail.CodeVerification(code, to)){
            return new JsonResult<>(400,"验证码错误");
        }
        //更改密码
        if(userService.lambdaUpdate().eq(User::getEmail,to).set(User::getPassword,password).update()){
            return new JsonResult<>(200,"重置成功");
        }
        return new JsonResult<>(400,"重置失败");
    }

    //发送验证码
    @PostMapping(value = "/retrievePassword/email")
    public JsonResult<String> retrieveEmail(@Validated @NotBlank(message = "邮箱不能为空") String to)
    {
        // 检查邮箱是否已注册
        boolean exists = userService.lambdaQuery().eq(User::getEmail, to).exists();
        if (!exists) {
            return new JsonResult<>(400, "该邮箱未注册");
        }
        if(verificationEmail.EmailSend(to)){
            return new JsonResult<>(200,"发送成功");
        }
        return new JsonResult<>(400,"发送失败");
    }
}
