package com.onedoorway.project.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSenderService {
    private final String apiKey;

    @Autowired
    public EmailSenderService(@Value("${spring.sendgrid.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    public void sendEmail(String subject, String content, String to) throws IOException {
        log.info("Sending mail to {} with subject {}", to, subject);
        Email from = new Email("noreply@edstem.com");
        Content body = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, new Email(to), body);
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        sg.api(request);
        log.info("Successfully sent the mail");
    }
}
