package com.dfq.coeffi.util.notification.messages;


import com.amazonaws.services.apigateway.model.Model;

public interface EmailService {

    String sendEmail(String email, String message, String template, Model model);
}
