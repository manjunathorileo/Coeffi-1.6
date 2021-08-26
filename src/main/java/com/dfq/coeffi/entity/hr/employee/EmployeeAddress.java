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

@Setter
@Getter
@Entity
@Table(name="employee_address")
public class EmployeeAddress implements Serializable{

	private static final long serialVersionUID = 3791332691802492472L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length=25)
	private String area;
	
	@Column(length=8)
	private String pincode;
	
	@Column(length=10)
	private String floor;
	
	@Column(length=10)
	private String block;
	
	@Column(length=30)
	private String line;
	
	@Column(length=30)
	private String street;
	
	@Column(name="house_number",length=30)
	private String houseNumber;
	
	@Column(length=20)
	private String city;
	
	@Column(length=15)
	private String state;
	
	@Column(length=15)
	private String country;
	
	@Column(length=30)
	private String landmark;
	
	@Column(length=20)
	@Enumerated(EnumType.STRING)
	private AddressType addressType;
	
	@OneToOne(cascade =  CascadeType.ALL)
    private EmergencyContact emergencyContact;
	
	@JsonBackReference
    @ApiModelProperty(hidden=true)
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;
}