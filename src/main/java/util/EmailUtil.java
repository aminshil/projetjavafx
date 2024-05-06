package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailUtil {

    public static void sendEmail(String host, String port,
                                 final String username, final String password,
                                 String toAddress, String subject, String message) throws MessagingException {

        // Paramètres SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Créer une session avec authentification
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Créer un message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        msg.setSubject(subject);
        msg.setText(message);

        // Envoyer le message
        Transport.send(msg);
    }
}