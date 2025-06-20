package com.work.integratedDesign.utility;

import java.util.Random;

public class RandomUtils {
    //获取一个指定枚举类的随机枚举值
    public static <T extends Enum<T>> T getRandomEnum(Class<T> clazz) {
        Random random = new Random();
        T[] constants = clazz.getEnumConstants();
        return constants[random.nextInt(constants.length)];
    }

    //    生成一个函数，它传入两个参数，第一个参数是随机数的下限，第二个参数是随机数的上限，返回一个随机数，这个随机数在两个参数之间。
    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}