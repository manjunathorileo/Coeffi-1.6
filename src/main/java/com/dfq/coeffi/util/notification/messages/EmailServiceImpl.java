package com.dfq.coeffi.util.notification.messages;

import com.amazonaws.services.apigateway.model.Model;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public String sendEmail(String email, String message, String template, Model model) {
        return null;
    }
}
