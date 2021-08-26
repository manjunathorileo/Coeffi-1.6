package com.dfq.coeffi.entity.communication;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmailLog{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private String recipient;
	
	@Column
	private String subject;
	
	@Column
	private String message;
	
	@Column
	private String studentName;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private CommunicationStatus communicationStatus;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern ="yyyy-MM-dd")
	private Date date;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Circular circular;
}
