package com.lianshidai.bcebe.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianshidai.bcebe.Pojo.Score;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;


public interface ScoreMapper extends BaseMapper<Score> {
    @Insert("INSERT INTO `score` (student_id, score, direction, problem_id, username) " +
            "SELECT #{studentId}, #{score}, #{direction}, #{problemId}, user.username " +
            "FROM `user` " +
            "WHERE user.student_id = #{studentId} " +
            "ON DUPLICATE KEY UPDATE score = VALUES(score)")
    void insertOrUpdateScore(@Param("studentId") Long studentId,
                             @Param("score") Integer score,
                             @Param("direction") String direction,
                             @Param("problemId") Integer problemId);
}
