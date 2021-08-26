package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class EventDto {

	public String title;
	public String message;
	public Date eventDate;
	public String eventName;
	public String eventIncharge;
	public String venue;
	public String refName;
	public List<Long> stdIds;
	public List<String> communicationChannels;
	public List<Long> empIds;
	public boolean isEmail;
	public boolean isSMS;
	public boolean isPushNotification;
	public Employee employee;
	private String message1;
	private String message2;
	private String message3;
	private String message4;


}