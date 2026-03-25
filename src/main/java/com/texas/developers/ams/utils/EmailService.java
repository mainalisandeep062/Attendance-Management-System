package com.texas.developers.ams.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Sends email using Thymeleaf template
     *
     * @param to            recipient email
     * @param subject       email subject
     * @param templateName  Thymeleaf template filename (without .html)
     * @param templateModel key-value map for template variables
     */
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> templateModel) throws MessagingException {
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariables(templateModel);

        // Process template
        String htmlContent = templateEngine.process(templateName, context);

        // Prepare MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom("Texas 'intl college <%s>".formatted(fromEmail));
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }
}