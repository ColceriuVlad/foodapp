package com.company.foodapp.services;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.models.Email;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    private PropertiesFileReader propertiesFileReader;
    private Properties properties;
    private Session session;
    private Logger logger;

    @Autowired
    public EmailService(PropertiesFileReader propertiesFileReader, Logger logger) {
        this.propertiesFileReader = propertiesFileReader;
        properties = getProperties();
        session = getSession();
        this.logger = logger;
    }

    private Properties getProperties() {
        var properties = System.getProperties();
        properties.put("mail.smtp.host", propertiesFileReader.getProperty("GMAIL_HOST"));
        properties.put("mail.smtp.port", propertiesFileReader.getProperty("GMAIL_PORT"));
        properties.put("mail.smtp.ssl.enable", propertiesFileReader.getProperty("GMAIL_SSL"));
        properties.put("mail.smtp.auth", propertiesFileReader.getProperty("GMAIL_AUTH"));
        return properties;
    }

    private Session getSession() {
        var session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                var from = propertiesFileReader.getProperty("GMAIL_FROM");
                var password = propertiesFileReader.getProperty("GMAIL_PASSWORD");
                return new PasswordAuthentication(from, password);
            }
        });

        return session;
    }

    public void sendMessage(Email email) throws MessagingException {
        var mimeMessage = new MimeMessage(session);

        try {
            var from = propertiesFileReader.getProperty("GMAIL_FROM");
            var fromInternetAddress = new InternetAddress(from);
            mimeMessage.setFrom(fromInternetAddress);

            var toInternetAddress = new InternetAddress(email.to);
            mimeMessage.addRecipient(Message.RecipientType.TO, toInternetAddress);

            mimeMessage.setSubject(email.subject);

            mimeMessage.setText(email.text);

            Transport.send(mimeMessage);
            logger.info("Email was sent to " + propertiesFileReader.getProperty("GMAIL_FROM"));
        } catch (MessagingException messagingException) {
            logger.info("Email could not be sent");
            throw messagingException;
        }
    }
}
