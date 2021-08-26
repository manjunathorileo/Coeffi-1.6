package com.dfq.coeffi.entity.hr.employee;
/*
 * @author Ashvini B
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name="emergency_contact")
public class EmergencyContact implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length=30)
	private String contactPersonName;
	
	@Column(length=15)
	private String contactPersonNumber;

    public EmergencyContact() {
		//default
    }
}