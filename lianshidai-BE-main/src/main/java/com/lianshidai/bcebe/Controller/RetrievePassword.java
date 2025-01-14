package com.lianshidai.bcebe.Controller;

import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Service.Impl.VerificationEmailImpl;
import com.lianshidai.bcebe.Service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

//重置密码
@RestController
public class RetrievePassword {
    @Resource
    private UserService userService;
    @Resource
    private VerificationEmailImpl verificationEmail;

    //重置密码
    @RequestMapping(value = "/retrievePassword",method = POST)
    @Transactional
    public JsonResult<String> retrievePassword(@NotBlank(message = "邮箱不能为空") String to,@NotBlank(message = "验证码不能为空") String code, String password)
    {
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
    @RequestMapping(value = "/retrievePassword/email",method = POST)
    public JsonResult<String> retrieveEmail(String to)
    {
        if(userService.lambdaQuery().eq(User::getEmail,to).one()==null){
            return new JsonResult<>(400,"该邮箱未注册");
        }
        if(verificationEmail.EmailSend(to)){
            return new JsonResult<>(200,"发送成功");
        }
        return new JsonResult<>(400,"发送失败");
    }
}
