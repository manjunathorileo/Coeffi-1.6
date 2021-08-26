package com.dfq.coeffi.util.notification.messages.email;

import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface MailService {
	public void sendEmail(Mail mail, String template);
	public void sendEmailWithAttachment(Mail mail, String template, File file) throws IOException;
	public void sendAluminiEmail(Mail mail, String template);
}
