package be.lennix.beveiligingproject;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


public class EmailSender {

    // code deels gebruikt van https://www.tutorialspoint.com/java/java_sending_email.htm

    private String to;
    private String from;

    public EmailSender(){

        setRecipient("lenni.beveiliging@gmail.com");
        setSender("lenni.beveiliging@gmail.com");
    }
    public EmailSender(String recipient, String sender){

        setRecipient(recipient);
        setSender(sender);
    }

    private Session setUpSession(){

        String host = "smtp.gmail.com";
        String port = "587";

        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        properties.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("lenni.beveiliging@gmail.com", "dierickx");
            }
        };

        return Session.getDefaultInstance(properties, authenticator);
    }

    public void sendEmail(String subject, String text){

        System.out.println("\nSubject: " + subject);
        System.out.println("\n\n" + text + "\n");

        try {
            MimeMessage message = new MimeMessage(setUpSession());

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);

            message.setText(text); // actual message

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void setSender(String sender){
        this.from = sender;
    }

    public void setRecipient(String recipient){
        this.to = recipient;
    }

    public String getRecipient(){

        return this.to;
    }

    public String getSender(){

        return this.from;
    }
}
