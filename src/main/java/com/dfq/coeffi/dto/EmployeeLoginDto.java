package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class EmployeeLoginDto {
	private long empId;
	private String email;
	private String password;
	private long roleId;
	private long userId;
	private String firstName;

	private Date fromDate;
	private Date toDate;

	private List<Long> roleIds;

	private List<Long> documentIds;
}
