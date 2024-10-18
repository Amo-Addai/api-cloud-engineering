package com.example.demo.controllers;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@RestController
public class EmailController {

    // TODO: switch-on "allow less secure apps" in "gmail" accounts if sender gmail(s) not sending by default

    @RequestMapping(path="/mail")
    public String sendMail() {
        return "Email sent";
    }

    @RequestMapping(path="/mail-attachment")
    public String sendMail1()
            throws AddressException, MessagingException, IOException {
        this.sendAttachmentMail();
        return "Email sent successfully";
    }

    private void sendAttachmentMail()
        throws AddressException, MessagingException, IOException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // todo: check why Props 'hashmap' also takes all non-string data-type values as strings
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session =
                Session.getInstance(
                        props,
                        new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(
                                        "auto@email.com", "pass"
                                );
                            }
                        }
                );

        Message msg = new MimeMessage(session);
        msg.setFrom(
                new InternetAddress(
                        "auto@email.com",
                        false
                )
        );
        msg.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(
                        "auto@email.com"
                )
        );
        msg.setSubject("Subject");
        msg.setContent(
                "Content",
                "text/html"
        );
        msg.setSentDate(new Date());

        MimeBodyPart msgBodyPart = new MimeBodyPart();
        msgBodyPart.setContent(
                "Content",
                "text/html"
        );

        Multipart multipart = new MimeMultipart();
        // 1st, add message body-part to multipart
        multipart.addBodyPart(msgBodyPart);

        MimeBodyPart attachPart = new MimeBodyPart();
        attachPart.attachFile("/path/to/file.png"); // todo

        // now, add attachment body-part to multipart
        multipart.addBodyPart(attachPart);
        // then add entire multipart to main (Message) msg obj's content
        msg.setContent(multipart);

        // send message
        Transport.send(msg);

    }

}
