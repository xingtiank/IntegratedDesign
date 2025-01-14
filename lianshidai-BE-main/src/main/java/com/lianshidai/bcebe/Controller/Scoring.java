package com.lianshidai.bcebe.Controller;

import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Service.UserService;
import jakarta.annotation.Resource;
import com.lianshidai.bcebe.Pojo.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


//评分
@RestController
public class Scoring {
    @Resource
    private UserService userService;
    @RequestMapping(value = "internal/scoring/system/blockchain",method = POST)
    @Transactional
    public JsonResult<String> blockchain(String id,String score)
    {
        userService.lambdaUpdate().eq(User::getId,id).set(User::getScore,score).update();
        return new JsonResult<>(200,"评分成功");
    }

}
