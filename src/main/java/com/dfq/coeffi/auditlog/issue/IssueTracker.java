package com.dfq.coeffi.auditlog.issue;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
public class IssueTracker implements Serializable
{
	private static final long serialVersionUID = -8044937119113724010L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date trackedOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "created_on", updatable = false)
	private Date createdOn;
	
	private String moduleName;
	
	@Column(length = 10000)
	private String errorStack;
	
	private boolean status;

	@Enumerated(EnumType.STRING)
	private Priority priority;
}