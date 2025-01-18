package com.lianshidai.bcebe.Pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Score {
    private Long studentId;
    private Integer score;
    private Integer problemId;
    private String direction;
    private String username;
}
