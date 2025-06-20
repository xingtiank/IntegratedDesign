package com.work.integratedDesign.pojo;


import lombok.*;


import java.util.Random;


@Data
@NoArgsConstructor
public class Goods {
    // 属性
    private double volume;       // 体积，单位平方米
    private double weight;       // 质量,单位吨
    private Place origin;       // 起始地
    private Place destination;  // 目的地
    private double[][] polyline; //路径
    // 随机数生成器
    private Random random = new Random();

    public Goods(double volume, Place origin, Place destination) {
        this.volume = volume;
        // 更具体积随机生成质量，质量随机浮动在-5到5吨之间
        this.weight = volume * 0.5 + 3 + (random.nextDouble() - 0.5) * 10;
        this.origin = origin;
        this.destination = destination;
    }
}
