package com.dfq.coeffi.entity.communication;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CommunicationConfiguration{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private String hostName;
	
	@Column
	private String email;
	
	@Column
	//@ColumnTransformer(read="DECRYPT(password)",write="encrypt(?)")
	private String password;
	
	@Column
	private int smtpPort;
	
	@Column
	private String smsAuthKey;
	
	@Column
	private String senderId;
	
	@Column
	private String apiUrl;

	@Column
	private boolean status;
}