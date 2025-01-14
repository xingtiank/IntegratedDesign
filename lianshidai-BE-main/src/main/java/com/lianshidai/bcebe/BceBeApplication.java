package com.lianshidai.bcebe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.lianshidai.bcebe.Mapper")
public class BceBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BceBeApplication.class, args);
    }

}
