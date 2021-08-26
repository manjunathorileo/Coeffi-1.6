package com.dfq.coeffi.resource;

import com.dfq.coeffi.entity.communication.CommunicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailLogResource {

	private String recipient;
	private String subject;
	private String message;
	private CommunicationStatus communicationStatus;
	private Long circularId;
	private List<Long> studentId;
}
