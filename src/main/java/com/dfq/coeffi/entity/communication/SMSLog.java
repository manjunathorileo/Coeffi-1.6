package com.dfq.coeffi.entity.communication;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class SMSLog{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private Long recipient;
	
	@Column(length = 500)
	private String message;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private CommunicationStatus communicationStatus;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Circular circular;
}
