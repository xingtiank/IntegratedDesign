package com.lianshidai.bcebe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;


@SpringBootApplication
@MapperScan("com.lianshidai.bcebe.Mapper")
@Validated
public class BceBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BceBeApplication.class, args);
    }

}
