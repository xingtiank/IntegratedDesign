package com.lianshidai.bcebe.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianshidai.bcebe.Pojo.Score;



public interface ScoreService extends IService<Score> {
    void insertOrUpdateScore(Long studentId, Integer score, String direction, Integer problemId);

}
