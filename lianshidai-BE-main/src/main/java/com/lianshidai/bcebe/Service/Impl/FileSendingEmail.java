package com.lianshidai.bcebe.Service.Impl;

import com.lianshidai.bcebe.Service.EmailService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

// 发送招新文件到指定邮箱
@Slf4j
@Service
public class FileSendingEmail implements EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSenderImpl mailSender;
    @Resource
    private  Environment env;


    public void sendEmailWithAttachment(String department, MultipartFile file) {
        if (file.getOriginalFilename() == null){
            throw new RuntimeException("文件名为空");
        }
        if (file.isEmpty()){
            throw new RuntimeException("文件为空");
        }

        String recipient = env.getProperty("mail-reception."+department+".recipient");
        String to = env.getProperty("mail-reception."+department+".to");
        if(to == null){
            throw new RuntimeException("没有找到接收者邮箱");
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true 表示支持多部分消息（multipart）
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("链时代招新 - " + recipient);
            helper.setText("请查收附件中的文件。");

            ByteArrayResource resource = new ByteArrayResource(file.getBytes());
            helper.addAttachment(file.getOriginalFilename(), resource);
            mailSender.send(mimeMessage);
//            log.info("文件邮件发送成功，接收者: {}", to);
        } catch (MessagingException | IOException e) {
//            log.error("文件邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("文件邮件发送失败");
        }
    }
}
