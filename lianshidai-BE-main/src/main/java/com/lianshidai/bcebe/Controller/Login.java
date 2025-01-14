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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
//登录
@RestController
@Validated
public class Login {
    @Resource
    private UserService userService;
    @RequestMapping(value = "/login",method = POST)
    public JsonResult<UserDto> login(@NotNull(message = "账号不能为空") @RequestParam("id") Integer id, @NotBlank(message = "密码不能为空") @RequestParam("password") String password, HttpServletResponse response){
        // 对密码进行SHA-256加密
        String encryptedPassword = EncryptPassword.encryptPassword(password);
        // 使用加密后的密码进行查询
        User foundUser = userService.lambdaQuery().eq(User::getId, id).eq(User::getPassword, encryptedPassword).one();
        if (foundUser != null) {
            UserDto userDto = new UserDto(foundUser.getId(),foundUser.getUsername(),foundUser.getName(),foundUser.getStudentID(),foundUser.getPhone(),foundUser.getEmail(),foundUser.getStatus());
            response.setHeader("token", JWTUtils.getToken(userDto));
            return new JsonResult<>(200, "登录成功",userDto);
        } else {
            return new JsonResult<>(401, "账号或密码错误");
        }
    }
}
