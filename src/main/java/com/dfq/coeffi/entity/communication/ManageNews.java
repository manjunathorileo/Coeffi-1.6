package com.dfq.coeffi.entity.communication;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@ToString
@Getter
@Setter
@Table(name="manage_news")
public class ManageNews implements Serializable
{

	private static final long serialVersionUID = 1058071393719199659L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(length=40)
	private String title;
	
	@Column(length=40)	
	private String author;
	
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name="posted_Date")
	private Date postedDate;

	
	@Column(length=40)
	private String content;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_On")	
	private Date createdOn;

	@Column(length=40)
	private String venue;

	@Column(length=40)
	private String refName;
}
