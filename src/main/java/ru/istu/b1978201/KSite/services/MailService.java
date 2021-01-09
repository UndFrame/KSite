package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.istu.b1978201.KSite.mode.User;

import java.util.concurrent.CompletableFuture;

/**
 * Сервеис для работы с почтой(отправление сообщения на почту)
 */
@Service
public class MailService {



    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void sendMail(String email, String subject, String message){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        mailSender.send(simpleMailMessage);
    }

    public void sendRegisterToken(User user){
        if (!StringUtils.isEmpty(user.getEmail())) {
            String messageBuilder =
                    "Чтобы подтверить регистрацию нужно перейти по ссылке: " +
                            "http://localhost:8080/activate?token=" +
                            user.getToken().getToken();
            CompletableFuture.runAsync(() -> {
                sendMail(user.getEmail(), "Код активации", messageBuilder);
            });
        }
    }

}
