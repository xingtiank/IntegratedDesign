package com.lianshidai.bcebe;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

public class JwtTest {
    @Test
    public void jwt1(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,20);
        String jwt = JWT.create()
//                .withHeader()
                .withClaim("username","lianshidai")
                .withClaim("id",123456)
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256("XIAOXING"));
        System.out.println(jwt);

    }
    @Test
    public void jwt2() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImxpYW5zaGlkYWkiLCJpZCI6MTIzN" +
                "DU2LCJleHAiOjE3MzY4NDI2OTh9.sTV2oBBMYoF9-L3nfR_eX-kqfcA78JMX4i9qIPMg4as";
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("XIAOXING")).build();
        DecodedJWT verify = jwtVerifier.verify(jwt);
        System.out.println(verify.getHeader());
        System.out.println(verify.getClaim("username").asString());
        System.out.println(verify.getClaim("id").asInt());
        System.out.println(verify.getExpiresAt());

    }
}
