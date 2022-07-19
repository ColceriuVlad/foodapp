package com.company.foodapp.services;

import com.company.foodapp.models.Email;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    private Environment environment;
    private Properties properties;
    private Session session;
    private Logger logger;

    @Autowired
    public EmailService(Environment environment, Logger logger) {
        this.environment = environment;
        properties = getProperties();
        session = getSession();
        this.logger = logger;
    }

    private Properties getProperties() {
        var properties = new Properties();
        properties.put("mail.smtp.host", environment.getProperty("GMAIL_HOST"));
        properties.put("mail.smtp.port", environment.getProperty("GMAIL_PORT"));
        properties.put("mail.smtp.ssl.enable", environment.getProperty("GMAIL_SSL"));
        properties.put("mail.smtp.auth", environment.getProperty("GMAIL_AUTH"));
        return properties;
    }

    private Session getSession() {
        var session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                var from = environment.getProperty("GMAIL_FROM");
                var password = environment.getProperty("GMAIL_PASSWORD");
                return new PasswordAuthentication(from, password);
            }
        });

        return session;
    }

    public boolean sendMessage(Email email) {
        var mimeMessage = new MimeMessage(session);

        try {
            var from = environment.getProperty("GMAIL_FROM");
            var fromInternetAddress = new InternetAddress(from);
            mimeMessage.setFrom(fromInternetAddress);

            var toInternetAddress = new InternetAddress(email.to);
            mimeMessage.addRecipient(Message.RecipientType.TO, toInternetAddress);

            mimeMessage.setSubject(email.subject);

            mimeMessage.setText(email.text);

            Transport.send(mimeMessage);
            logger.info("Email was sent to " + environment.getProperty("GMAIL_FROM"));
            return true;
        } catch (MessagingException messagingException) {
            logger.info("Email could not be sent");
            return false;
        }
    }
}
