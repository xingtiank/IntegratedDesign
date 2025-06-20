package com.work.integratedDesign.service.Impl;

import com.work.integratedDesign.service.EmailSendingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailSendingServiceImpl implements EmailSendingService {
    @Value("${spring.mail.username}")
    private String form;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    public void VerificationCodeSending(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(form);
        message.setTo(to);
        message.setSubject("IntegratedDesign验证码");
        Random random = new Random();
        int num = random.nextInt(900000) + 100000;
        String content = "你的验证码为:" + num + "请妥善保管，不要泄露。";
        message.setText(content);
        redisTemplate.opsForValue().set(to,String.valueOf(num),3, TimeUnit.MINUTES);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.info("邮件发送失败:{}",e.getMessage());
            throw new RuntimeException("邮件发送失败");
        }
    }
    // 验证码验证
    public  boolean CodeVerification(@NotNull String code, @NotNull String to){
        return Objects.equals(redisTemplate.opsForValue().get(to), code);
    }
}