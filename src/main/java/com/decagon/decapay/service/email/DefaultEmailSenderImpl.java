package com.decagon.decapay.service.email;


import com.decagon.decapay.config.email.EmailConfig;
import com.decagon.decapay.payloads.request.email.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.util.Properties;
@RequiredArgsConstructor
@Component
@Slf4j
@Profile({"dev","test"})
public class DefaultEmailSenderImpl implements EmailSender {

    private final JavaMailSender emailSender;
    private EmailConfig emailConfig;


    @Override
    public void send(Email email){
        
        log.info("Begin sending email ");
        final String eml = email.getFrom();
        final String from = email.getFromEmail();
        final String to = email.getTo();
        final String subject = email.getSubject();
        final Email.FileAttachement attachement = email.getAttachement();

        MimeMessagePreparator preparator = mimeMessage -> {

            JavaMailSenderImpl impl = (JavaMailSenderImpl) emailSender;
            // if email configuration is present in Database, use the same
            if (emailConfig != null) {
                impl.setProtocol(emailConfig.getProtocol());
                impl.setHost(emailConfig.getHost());
                impl.setPort(Integer.parseInt(emailConfig.getPort()));
                impl.setUsername(emailConfig.getUsername());
                impl.setPassword(emailConfig.getPassword());

                Properties prop = new Properties();
                prop.put("mail.smtp.auth", emailConfig.isSmtpAuth());
                prop.put("mail.smtp.starttls.enable", emailConfig.isStarttls());
                impl.setJavaMailProperties(prop);
            }

            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            InternetAddress inetAddress = new InternetAddress();

            inetAddress.setPersonal(eml);
            inetAddress.setAddress(from);

            mimeMessage.setFrom(inetAddress);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(email.getBody(), "utf-8", "html");

             if(attachement!=null){
                 MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                 messageHelper.addAttachment(attachement.getFileName(), new ByteArrayResource(IOUtils.toByteArray(attachement.getInputStream())),"application/pdf");
                 messageHelper.setText(email.getBody(), true);
             }

        };
        emailSender.send(preparator);
    }

    @Override
    public void setEmailConfig(EmailConfig emailConfig) {
       this.emailConfig=emailConfig;
    }
}
