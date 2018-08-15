package com.gamesbykevin.linkedinbot.util;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static com.gamesbykevin.linkedinbot.util.LogFile.displayMessage;

public class Email extends Thread {

    public static String EMAIL_NOTIFICATION_ADDRESS = null;

    public static String GMAIL_SMTP_USERNAME = null;
    public static String GMAIL_SMTP_PASSWORD = null;

    //the subject and body of our email message
    private final String subject, body;

    public Email(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBody() {
        return this.body;
    }

    @Override
    public void run() {

        //don't send email if email address doesn't exist
        if (EMAIL_NOTIFICATION_ADDRESS == null || EMAIL_NOTIFICATION_ADDRESS.trim().length() < 5)
            return;

        if (GMAIL_SMTP_USERNAME == null || GMAIL_SMTP_USERNAME.trim().length() < 5)
            return;
        if (GMAIL_SMTP_PASSWORD == null || GMAIL_SMTP_PASSWORD.trim().length() < 5)
            return;

        Message message = null;
        Properties props = null;
        Session session = null;

        try {

            //setup our google properties
            props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            //use google's smtp servers to send emails
            session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(GMAIL_SMTP_USERNAME, GMAIL_SMTP_PASSWORD);
                }
            });

            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(GMAIL_SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_NOTIFICATION_ADDRESS));
            message.setSubject(getSubject());
            message.setText(getBody());

            //we are now sending
            displayMessage("Sending email....");

            //send the email
            Transport.send(message);

            //display we are good
            displayMessage("Sent message successfully.");

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                message = null;
                props = null;
                session = null;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void send(final String subject, final String body) {

        //create new email, which creates a new thread
        Email email = new Email(subject, body);

        //start the thread which will send the email
        email.start();
    }
}