package com.lianshidai.bcebe.Controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Service.ScoreService;
import com.lianshidai.bcebe.Service.UserService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.lianshidai.bcebe.Pojo.Score;

@RestController
public class Marking {
    @Resource
    private ScoreService scoreService;
    @Resource
    private UserService userService;
    @GetMapping("/marking/{StudentId}/{direction}/{problemId}/{score}")
    @Transactional(rollbackFor = {Exception.class}) // 开启事务
    public JsonResult<String> marking(@PathVariable("StudentId")Long studentId,@PathVariable("direction")String direction,
                                      @PathVariable("problemId")Integer problemId,@PathVariable("score")Integer score){
        Score one = scoreService.lambdaQuery().select(Score::getScore)
                .eq(Score::getStudentId, studentId)
                .eq(Score::getProblemId, problemId).one();
        Integer oldScore = one != null ? one.getScore() : 0;
        scoreService.insertOrUpdateScore(studentId, score, direction, problemId);
        //更新用户总分和方向总分
        score=score-oldScore;
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<User>()
                .setSql("score_total = score_total + " + score + ", " + "score_"+ direction +" = score_" + direction +" + " + score)
                .eq("student_id", studentId);
        userService.update(userUpdateWrapper);
        return new JsonResult<>(200, "标记成功");
    }
}
