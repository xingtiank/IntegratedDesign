package com.lianshidai.bcebe.Controller;


import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.Score;
import com.lianshidai.bcebe.Service.ScoreService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SingleQuery {
    @Resource
    private ScoreService scoreService;
    @PostMapping(value = "/singleQuery")
    public JsonResult<List<Score>> singleQuery(@RequestParam("StudentID")Long StudentID){
        List<Score> records =scoreService.lambdaQuery().select(Score::getScore,Score::getProblemId,Score::getDirection,Score::getUsername)
                .eq(Score::getStudentId,StudentID)
                .list();
        if(records.isEmpty()){
            return new JsonResult<>(400,"查询为空");
        }
        return new JsonResult<>(200,"查询成功",records);
    }
}
