package com.work.integratedDesign.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 当浏览器访问 /output/xxx 时，会去 "D:/JavaProject/IntegratedDesign/output/" 目录下找对应的xxx文件
        registry.addResourceHandler("/output/**")
                .addResourceLocations("file:D:/JavaProject/IntegratedDesign/output/");
    }
}