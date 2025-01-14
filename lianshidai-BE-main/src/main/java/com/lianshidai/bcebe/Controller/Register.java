package com.lianshidai.bcebe.Controller;

import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.RegisterGet;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Service.Impl.VerificationEmailImpl;
import com.lianshidai.bcebe.Service.UserService;
import com.lianshidai.bcebe.Utils.EncryptPassword;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
// 注册
@Slf4j
@Validated
@RestController
public class Register {
    @Resource
    private UserService userService;
    @Resource
    private VerificationEmailImpl verificationEmail;

    // 注册
    @RequestMapping(value = "/register",method = POST)
    @Transactional
    public JsonResult<Integer> register(@Valid @NotNull(message = "信息不能为空")  @RequestBody RegisterGet registerGet){
        String code = registerGet.getCode();
        User user= registerGet.getUser();
        if(!verificationEmail.CodeVerification(code, user.getEmail())){
            return new JsonResult<>(400,"验证码错误");
        }
        if(userService.lambdaQuery().eq(User::getStudentID,user.getStudentID()).one()!=null){
            return new JsonResult<>(400,"该学号已注册");
        }
        user.setPassword(EncryptPassword.encryptPassword(user.getPassword()));
        user.setStatus(0);
        userService.save(user);
        //获取用户id
        User savedUser = userService.lambdaQuery().eq(User::getStudentID, user.getStudentID()).one();
        if (savedUser != null) {
            return new JsonResult<>(200, "注册成功", savedUser.getId());
        } else {
            log.info("保存用户后未找到用户ID");
            return new JsonResult<>(500, "注册失败");
        }
    }
    // 发送验证码
    @RequestMapping(value = "/register/email",method = POST)
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
