package com.campusrecruit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {
    private final JavaMailSender sender;
    private final String from;
    private final String supportAddress;
    private final boolean smtpEnabled;
    private final boolean developmentCodeEnabled;
    public EmailService(JavaMailSender sender,
                        @Value("${app.email.from:career-services@campusrecruit.local}") String from,
                        @Value("${app.email.support-address:career-services@campusrecruit.local}") String supportAddress,
                        @Value("${app.email.smtp-enabled:false}") boolean smtpEnabled,
                        @Value("${app.email.development-code-enabled:true}") boolean developmentCodeEnabled) {
        this.sender=sender; this.from=from; this.supportAddress=supportAddress;
        this.smtpEnabled=smtpEnabled; this.developmentCodeEnabled=developmentCodeEnabled;
    }
    public boolean sendOtp(String email,String code,String purpose) {
        if(!smtpEnabled) return false;
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from); message.setTo(email); message.setSubject("CampusRecruit verification code");
        message.setText("Your verification code for "+purpose.toLowerCase().replace('_',' ')+" is "+code+". It expires in 10 minutes. If you did not request this, ignore this message.");
        sender.send(message); return true;
    }
    public boolean canShowDevelopmentCode(){return !smtpEnabled&&developmentCodeEnabled;}
    public boolean sendSupport(String name,String email,String subject,String body) {
        if(!smtpEnabled) return false;
        SimpleMailMessage message=new SimpleMailMessage(); message.setFrom(from); message.setTo(supportAddress);
        message.setReplyTo(email); message.setSubject("CampusRecruit support: "+subject);
        message.setText("From: "+name+" <"+email+">\n\n"+body);
        sender.send(message); return true;
    }
    public String supportAddress(){return supportAddress;}
}
