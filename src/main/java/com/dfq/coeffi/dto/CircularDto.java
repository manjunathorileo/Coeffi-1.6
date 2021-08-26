package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CircularDto {
	
	public String title;
	public String message;
	public Date circularDate;
	public String refName;
	public List<Long> stdIds;
    List<String> communicationChannels;
	public List<Long> empIds;
	public List<Long> alumniId;
	public boolean isEmail;
	public boolean isSMS;
	public boolean isPushNotification;
	public long documentId;


}
