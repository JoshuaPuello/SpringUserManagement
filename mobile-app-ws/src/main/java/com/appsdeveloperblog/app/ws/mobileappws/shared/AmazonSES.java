package com.appsdeveloperblog.app.ws.mobileappws.shared;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class AmazonSES {

    final String FROM = "joshuapp96@gmail.com";

    final String SUBJECT = "One last step to complete your registration with PhotoApp";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='http://ec2-18-231-115-232.sa-east-1.compute.amazonaws.com:8090/verification-service/email-verification.html?token=$tokenValue'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";

    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " http://ec2-18-231-115-232.sa-east-1.compute.amazonaws.com:8090/verification-service/email-verification.html?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";


    final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='http://ec2-18-231-115-232.sa-east-1.compute.amazonaws.com:8090/verification-service/password-reset.html?token=$tokenValue'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";

    final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " http://ec2-18-231-115-232.sa-east-1.compute.amazonaws.com:8090/verification-service/password-reset.html?token=$tokenValue"
            + " Thank you!";


    private AmazonSimpleEmailService createSESClient() {

        return AmazonSimpleEmailServiceClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .build();

    }

    public void verifyEmail(UserDTO userDto) {

        AmazonSimpleEmailService client = createSESClient();

        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
        String textBodyWithToken = TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);

        System.out.println("Email sent!");

    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token) {

        boolean returnValue = false;

        AmazonSimpleEmailService client = createSESClient();

        String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);

        String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
        textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);


        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                .withSource(FROM);

        SendEmailResult result = client.sendEmail(request);

        if (result != null && (result.getMessageId() != null && !result.getMessageId().isEmpty())) {
            returnValue = true;
        }

        return returnValue;
    }

}