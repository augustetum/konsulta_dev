package com.konsulta.application.data.service;

import javax.mail.Session;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587"); // Adjust the port as needed
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        final String username = "konsulta.consultations@gmail.com";
        final String password = "hfkjliyrhldodgem";

        Session session = Session.getInstance(properties, new EmailAuthenticator(username, password));

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
    }
}

