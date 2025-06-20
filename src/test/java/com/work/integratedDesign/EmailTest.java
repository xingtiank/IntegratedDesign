package com.work.integratedDesign;

import com.work.integratedDesign.service.EmailSendingService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailTest {
    @Resource
    private EmailSendingService emailSendingService;
    @Test
    public void test() {
        emailSendingService.VerificationCodeSending("1829275415@qq.com");
    }
}
