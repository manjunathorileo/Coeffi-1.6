package com.dfq.coeffi.util.notification.messages.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages="com.dfq.coeffi.util.notification.messages.email.mail")
public class EmailConfig {

	public Mail setMailCredentials(String mailTo, String subject,String content, Map< String, Object > model)
	{
		Mail mail = new Mail();
	    mail.setMailFrom("orileotest@gmail.com");
	    mail.setMailTo(mailTo);
	    mail.setMailSubject(subject);
		mail.setMailContent(content);
	    mail.setModel(model);
	    return mail;
		}
	    @Bean
	    public JavaMailSender getMailSender() 
	    {
	        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();	 
	        mailSender.setHost("smtp.gmail.com");
	        mailSender.setPort(465);
	        mailSender.setUsername("orileotest@gmail.com");
	        mailSender.setPassword("yakanna@123");
	        Properties javaMailProperties = new Properties();
	        javaMailProperties.put("mail.smtp.starttls.enable", "true");
	        javaMailProperties.put("mail.smtp.auth", "true");
	        javaMailProperties.put("mail.transport.protocol", "smtps");
	        javaMailProperties.put("mail.debug", "true");
	        mailSender.setJavaMailProperties(javaMailProperties);
	        return mailSender;
	    }
	
}
