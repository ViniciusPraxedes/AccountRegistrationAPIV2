package com.example.accountregistrationv2.repositories;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String email);
}
