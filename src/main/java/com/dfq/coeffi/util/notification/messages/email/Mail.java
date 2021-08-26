package com.dfq.coeffi.util.notification.messages.email;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class Mail {
    private String mailFrom;

    private String mailTo;

    private String mailSubject;

    private String mailContent;

    private Map<String, Object> model;
}
