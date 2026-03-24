package com.texas.developers.ams.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestEmailSender {

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void sendTestEmail() throws MessagingException {
        Map<String, Object> model = new HashMap<>();
        model.put("name", "Yenya");
        model.put("username", "yenya123");
        model.put("password", "secret123");

        emailService.sendEmail(
                "suraj.maharjan@yenyasoft.com",
                "Registration Successful",
                "email/registrationsuccess",
                model
        );

        System.out.println("Email Sent!");
    }
}