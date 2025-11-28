package org.gamelog.utils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    private final Properties props;
    private final String senderEmail;
    private final String senderPassword;

    public EmailSender() {

        //Loads Credentials with Dotenv
        Dotenv dotenv = Dotenv.load();
        this.senderEmail = dotenv.get("MAIL_USERNAME");
        this.senderPassword = dotenv.get("MAIL_PASSWORD");

        //Sets up mail server properties
        props = new Properties();
        props.put("mail.smtp.host", dotenv.get("MAIL_HOST"));
        props.put("mail.smtp.port", dotenv.get("MAIL_PORT"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    public void sendVerificationEmail(String recipientEmail, String code){

        //Creates Session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);

            //Sets sender and recipient
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("GameLog Password Reset Verification Code");

            String emailContent =
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; color: #1C3849; line-height: 1.6;'>" +
                            "<h2 style='color: #1C3849;'>Password Reset Verification</h2>" +
                            "<p>You requested a password reset for your GameLog account. Your verification code is:</p>" +
                            "<div style='background-color: #F0F0F0; border-radius: 8px; padding: 20px; display: inline-block; margin: 15px 0;'>" +
                            "<h1 style='color: #4A5B85; margin: 0; font-size: 36px;'><b>" + code + "</b></h1>" +
                            "</div>" +
                            "<p>Please enter this code on the password reset page.</p>" +
                            "<p style='color: #cc0000; font-weight: bold;'>This code is only valid for 5 minutes.</p>" +
                            "<p style='margin-top: 30px; font-size: 1.1em;'>If you did not request a password reset, please ignore this email. Your password remains unchanged.</p>" +
                            "</body>" +
                    "</html>";

            message.setContent(emailContent, "text/html");

            Transport.send(message);

        }catch(MessagingException mex){
            mex.printStackTrace();
        }
    }
}