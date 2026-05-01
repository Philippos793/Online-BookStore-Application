package socialbookstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncEmailService {

    @Autowired
    private JavaMailSender mailSender;
    public AsyncEmailService(JavaMailSender emailSender) {
        this.mailSender = emailSender;
    }
    @Async
    public void sendEmail(SimpleMailMessage message) {
        mailSender.send(message);
    }
}
