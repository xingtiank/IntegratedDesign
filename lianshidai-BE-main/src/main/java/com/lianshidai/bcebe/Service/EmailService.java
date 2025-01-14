package com.lianshidai.bcebe.Service;


import org.springframework.web.multipart.MultipartFile;

public interface EmailService {
    void sendEmailWithAttachment(String department, MultipartFile file);
}
