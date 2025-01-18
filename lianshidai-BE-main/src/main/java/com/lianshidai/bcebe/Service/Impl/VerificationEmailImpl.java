package com.lianshidai.bcebe.Service.Impl;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//发送验证码
@Service
public class VerificationEmailImpl {
    @Value("${spring.mail.username}")
    private String from;
    @Resource
    private  JavaMailSenderImpl mailSender;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    public boolean EmailSend(@NotNull String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        List<String> list = List.of(to);
        message.setTo(list.toArray(new String[0]));
        message.setSubject("链时代");
        Random random = new Random();
        int num = random.nextInt(900000) + 100000;
        String content = "你的验证码为:" + num + "请妥善保管，不要泄露。";
        redisTemplate.opsForValue().set(to,String.valueOf(num),3, TimeUnit.MINUTES);
        message.setText(content);
        try {
            mailSender.send(message);
            return true;
        }catch (MailException e) {
            return false;
        }
    }
    public  boolean CodeVerification(@NotNull String code, @NotNull String to){
        return Objects.equals(redisTemplate.opsForValue().get(to), code);
    }

}
