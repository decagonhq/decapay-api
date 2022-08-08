package com.decagon.decapay.service.email;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.decagon.decapay.config.email.EmailConfig;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("sesEmailSender")
@Profile("prod")
public class SESEmailSenderImpl implements EmailSender {

    // The email body for recipients with non-HTML email clients.
    static final String TEXTBODY =
            "This email was sent through Amazon SES " + "using the AWS SDK for Java.";

    @Value("${amazonProperties.region}")
    private String region;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Override
    public void send(Email email) {


        Validate.notNull(region, "AWS region is null");
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                // Replace US_WEST_2 with the AWS Region you're using for
                // Amazon SES.
                // .withRegion(Regions.valueOf(region.toUpperCase())).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
                .withRegion(Regions.valueOf(region.toUpperCase())).build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email.getTo()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(email.getBody()))
                                .withText(new Content().withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content().withCharset("UTF-8").withData(email.getSubject())))
                .withSource(email.getFromEmail());
        // Comment or remove the next line if you are not using a
        // configuration set
        //.withConfigurationSetName(CONFIGSET);
        System.out.println("reqrequest/" + email);

        client.sendEmail(request);

    }

    @Override
    public void setEmailConfig(EmailConfig emailConfig) {

    }
}
