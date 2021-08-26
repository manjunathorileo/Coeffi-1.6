package com.dfq.coeffi.util.notification.messages;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.*;
import com.dfq.coeffi.auditlog.log.ApplicationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextMessageServiceImpl implements TextMessageService {

    private final String CLIENT_ID = "AKIAI6AJVTRHWV7DZQTQ";
    private final String CLIENT_SECRET = "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";

    @Autowired
    private ApplicationLogService applicationLogService;

    @Override
    public String sendTextMessage(String phoneNumber, String message) {

        AWSCredentials awsCredentials = new BasicAWSCredentials(CLIENT_ID, CLIENT_SECRET);
        final AmazonSNSClient client = new AmazonSNSClient(awsCredentials);
        client.setRegion(Region.getRegion(Regions.US_EAST_1));

        AmazonSNSClient snsClient = new AmazonSNSClient(awsCredentials);

        Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue("Transactional").withDataType("String"));
        PublishResult result = snsClient.publish(new PublishRequest().withMessage(message).withPhoneNumber("+91" + phoneNumber).withMessageAttributes(smsAttributes));
        if(result != null){
            String logMessage  = "SMS : " + message + " | PHONE : " +phoneNumber+ " | MESSAGE-ID : " +result.getMessageId();
            applicationLogService.recordApplicationLog("From Application", logMessage, "MESSAGE", 0);
        }

        return result.toString();
    }

    @Override
    public String sendTextMessageWithTopic(String phoneNumber, String message) {
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phoneNumber);
        AmazonSNSClient snsClient = new AmazonSNSClient(new BasicAWSCredentials(CLIENT_ID, CLIENT_SECRET));
        String topicArn = createSNSTopic(snsClient, "ORILEO");

        // Subcribe Phone Numbers to Topic
        subscribeToTopic(snsClient, topicArn, "sms", phoneNumbers);
        // Publish Message to Topic
        sendSMSMessageToTopic(snsClient, topicArn, message);

        return null;
    }

    public static String createSNSTopic(AmazonSNSClient snsClient,String topicName) {
        CreateTopicRequest createTopic = new CreateTopicRequest(topicName);
        CreateTopicResult result =  snsClient.createTopic(createTopic);
        return result.getTopicArn();
    }

    public static void subscribeToTopic(AmazonSNSClient snsClient, String topicArn, String protocol, List<String> phoneNumbers) {
        for (String phoneNumber : phoneNumbers) {
            SubscribeRequest subscribe = new SubscribeRequest(topicArn, protocol, phoneNumber);
            snsClient.subscribe(subscribe);
        }
    }

    public static String sendSMSMessageToTopic(AmazonSNSClient snsClient, String topicArn, String message) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(message));
        return result.getMessageId();
    }
}
