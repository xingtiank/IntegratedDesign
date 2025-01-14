package com.lianshidai.bcebe.Utils;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

//拦截器
public class JWTInterceptors implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response,@Nullable Object handler) throws Exception {
        Map<String,Object> map = new HashMap<>();
        //令牌建议是放在请求头中，获取请求头中令牌
        String token = request.getHeader("token");
        try{
            JWTUtils.JWTverify(token);//验证令牌
            return true;//放行请求
        } catch (SignatureVerificationException e) {
            map.put("msg","无效签名");
        } catch (TokenExpiredException e) {
            map.put("msg","token过期");
        } catch (AlgorithmMismatchException e) {
            map.put("msg","token算法不一致");
        } catch (Exception e) {
            map.put("msg","token失效");
        }
        map.put("code",401);
        map.put("state",false);//设置状态
        //将map转化成json，response使用的是Jackson
        String json = new ObjectMapper().writeValueAsString(map);
        if (response != null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(json);
        }
        return false;
    }
}

