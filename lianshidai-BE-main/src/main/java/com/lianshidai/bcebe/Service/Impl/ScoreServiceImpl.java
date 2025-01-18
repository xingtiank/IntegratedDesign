package com.lianshidai.bcebe.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianshidai.bcebe.Mapper.ScoreMapper;
import com.lianshidai.bcebe.Pojo.Score;
import com.lianshidai.bcebe.Service.ScoreService;
import org.springframework.stereotype.Service;


@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    @Override
    public void insertOrUpdateScore(Long studentId, Integer score, String direction, Integer problemId) {
        baseMapper.insertOrUpdateScore(studentId, score, direction, problemId);
    }
}
