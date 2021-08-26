package com.dfq.coeffi.util.notification.messages;

public interface TextMessageService {
    String sendTextMessage(String phoneNumber, String message);
    String sendTextMessageWithTopic(String phoneNumber, String message);
}