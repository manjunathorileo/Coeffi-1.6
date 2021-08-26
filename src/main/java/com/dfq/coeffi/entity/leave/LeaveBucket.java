package com.dfq.coeffi.entity.leave;

import com.dfq.coeffi.entity.master.AcademicYear;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@ToString
@Entity
@Table(name="leave_bucket")
public class LeaveBucket implements Serializable{
	
	private static final long serialVersionUID = -3293267259619667409L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private int noOfLeave;
	
	@Column
	private int casualLeave;
	
	@Column
	private int paidLeave;
	
	@Column
	private int unPaidLeave;
	
	@OneToOne(cascade=CascadeType.ALL)
	private AcademicYear academicYear;
}