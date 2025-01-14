package com.lianshidai.bcebe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {


    @Autowired
    private RedisTemplate redisTemplate;
    
    @Test
    public void test(){

        try{
            redisTemplate.opsForValue().set("77","10086",3, TimeUnit.MINUTES);
            String aa = (String) redisTemplate.opsForValue().get("77");
            //redisTemplate.delete("77");
            System.out.println(aa);
            Integer a = Integer.valueOf(aa);
            System.out.println(a);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
