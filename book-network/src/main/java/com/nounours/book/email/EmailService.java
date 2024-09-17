package com.nounours.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;
    @Autowired
    private  final SpringTemplateEngine templateEngine;

    // we need to add "@EnableAsync"  in the main class to make it works
    @Async // means the send of email take time and should not block the user from interacting with the App
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate, // from type enum
            String confirmationUrl,
            String activationCode,
            String subject

    ) throws MessagingException {
        String templateName ;
        if(emailTemplate == null){
            templateName ="confirm-email";
        }
        else{
            templateName = emailTemplate.name();
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        // pass some informations to our EmailTemplate which is an Html Template with a Map
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username); // the Strings ("username", "activation_code"..) must be the same as in the activate_account.html file
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        //thymeleaf context
        Context context = new Context();
        context.setVariables(properties);// pass the properties (infos) to  our template with a Map
        helper.setFrom("wdhanan03@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        //process the template
        String template = templateEngine.process(templateName, context);

        helper.setText(template, true);
        mailSender.send(mimeMessage);



    }

}
