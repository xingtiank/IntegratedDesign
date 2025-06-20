package com.work.integratedDesign;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.work.integratedDesign.mapper")
public class IntegratedDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegratedDesignApplication.class, args);
    }

}
