package com.lianshidai.bcebe;


import com.lianshidai.bcebe.Service.Impl.FileSendingEmail;
import com.lianshidai.bcebe.Service.Impl.VerificationEmailImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.File;
import java.util.List;
import java.util.Random;


@Slf4j
@SpringBootTest
public class EmailTest {

    @Value("${spring.mail.username}")
    private String form;
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Resource
    private VerificationEmailImpl verificationEmailImpl;
    @Resource
    private FileSendingEmail fileSendingEmail;
    @Test
    public void test() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(form);
        List<String> list = List.of("2164536159@qq.com", "tmy13779754349@qq.com");
        message.setTo(list.toArray(new String[list.size()]));
        message.setSubject("你好 测试邮件");
        Random random = new Random();
        int num = random.nextInt(900000) + 100000;
        String content = "你的验证码为:" + num + "请妥善保管，不要泄露。";
        message.setText(content);
        try {
            mailSender.send(message);
            log.info("发送成功");
        }catch (MailException e) {
            e.getMessage();
        }
    }
    @Test
    public void test2() {
        verificationEmailImpl.EmailSend("2164536159@qq.com");
    }
    @Test
    public void test3() {

    }

}
