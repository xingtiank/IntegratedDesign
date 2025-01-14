package com.lianshidai.bcebe.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lianshidai.bcebe.Pojo.UserDto;
import jakarta.validation.constraints.NotNull;


import java.util.Calendar;
import java.util.Map;
// jwt工具类
public class JWTUtils {
    private static final String secretKEY = "XIAOXIN";
    public static String getToken(@NotNull UserDto userDto) {

        Calendar instance = Calendar.getInstance();
        // 3天过期
        instance.add(Calendar.DATE, 3);

        JWTCreator.Builder builder = JWT.create();
        // header采用默认值
        // payload
//        map.forEach(builder::withClaim);
        builder.withClaim("id", userDto.getId())
                .withClaim("username", userDto.getUsername())
                .withClaim("name", userDto.getName())
                .withClaim("StudentID", userDto.getStudentID())
                .withClaim("phone", userDto.getPhone())
                .withClaim("email", userDto.getEmail())
                .withClaim("status", userDto.getStatus());
        return builder.withExpiresAt(instance.getTime())  //指定令牌过期时间
                .sign(Algorithm.HMAC256(secretKEY)); //
    }

    // 校验token合法性
    public static DecodedJWT JWTverify(@NotNull String token) {
        return JWT.require(Algorithm.HMAC256(secretKEY)).build().verify(token);
    }


    // 获取token信息方法
    public static DecodedJWT getTokenInfo(@NotNull String token){
        return JWT.require(Algorithm.HMAC256(secretKEY)).build().verify(token);
    }


}
