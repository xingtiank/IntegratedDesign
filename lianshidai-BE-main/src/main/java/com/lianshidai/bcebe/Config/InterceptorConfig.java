package com.lianshidai.bcebe.Config;


import com.lianshidai.bcebe.Utils.JWTInterceptors;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptors())
                .addPathPatterns("/file")  // 其他接口token验证
                .excludePathPatterns("/login", "/register", "/email");  // 所有用户都放行

    }

}
