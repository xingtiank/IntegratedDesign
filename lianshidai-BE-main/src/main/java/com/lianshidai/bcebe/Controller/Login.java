package com.lianshidai.bcebe.Controller;

import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Pojo.UserDto;
import com.lianshidai.bcebe.Service.UserService;
import com.lianshidai.bcebe.Utils.EncryptPassword;
import com.lianshidai.bcebe.Utils.JWTUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//登录
@RestController
@Validated
public class Login {
    @Resource
    private UserService userService;
    @PostMapping(value = "/login")
    public JsonResult<UserDto> login(@Validated @NotNull(message = "邮箱不能为空") @RequestParam("email") String email,
                                     @Validated @NotBlank(message = "密码不能为空") @RequestParam("password") String password, HttpServletResponse response){
        // 对密码进行SHA-256加密
        String encryptedPassword = EncryptPassword.encryptPassword(password);
        // 使用加密后的密码进行查询
        User foundUser = userService.lambdaQuery().eq(User::getEmail, email).eq(User::getPassword, encryptedPassword).one();
        if (foundUser != null) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(foundUser,userDto);
            response.setHeader("token", JWTUtils.getToken(userDto));
            return new JsonResult<>(200, "登录成功",userDto);
        } else {
            return new JsonResult<>(401, "密码错误");
        }
    }
}
