package com.lianshidai.bcebe.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class PageQuery {
    private static final Integer pageSize = 10;
    @Resource
    private UserService userService;
    @GetMapping(value = "/page/query/{pageNo}")
    public JsonResult<List<User>>pageQuery(@Validated @NotNull(message = "不能为空")@PathVariable("pageNo")Integer pageNo){
        Page<User> page = Page.of(pageNo,pageSize);
        page.orders().add(OrderItem.desc("score_total"));
        Page<User> p = userService.lambdaQuery().select(User::getUsername,User::getScoreTotal).page(page);
        List<User> records= p.getRecords();
        if(records.isEmpty()){
            return new JsonResult<>(400,"查询失败");
        }
        return new JsonResult<>(200,"查询成功",records);
    }
    @GetMapping(value = "/page/query/{direction}/{pageNo}")
    public JsonResult<List<User>> pageQueryMl(@Validated @NotBlank(message = "不能为空")@PathVariable("direction")String direction,
                                              @Validated @NotNull(message = "不能为空")@PathVariable("pageNo")Integer pageNo){
        Page<User> page = Page.of(pageNo,pageSize);
        page.orders().add(OrderItem.desc("score_"+direction));
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
                .select("username","score_"+direction);
        Page<User> p = userService.page(page, queryWrapper);
        List<User> records= p.getRecords();
        if(records.isEmpty()){
            return new JsonResult<>(400,"查询失败");
        }
        return new JsonResult<>(200,"查询成功",records);
    }

}
