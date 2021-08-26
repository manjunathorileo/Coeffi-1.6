package com.dfq.coeffi.entity.hr.employee;
/*
 * @author Ashvini B
 */

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="qualification")
public class Qualification implements Serializable{
	
	private static final long serialVersionUID = -8477514131515106343L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String course;
	
	private String courseType;
	
	private String branch;
	
	@Column
	private Date startDate;
	
	@Column
	private Date yearOfCompletion;
	
	@Column(length=10)
	private double aggregate;

	private String placeOfGraduation;
    
    @JsonBackReference
    @ApiModelProperty(hidden=true)
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;
		
	public Qualification(){
		//Def
	}
}