package com.work.integratedDesign.service;

public interface EmailSendingService {
    void VerificationCodeSending(String to);
    boolean CodeVerification(String code, String to);
}
