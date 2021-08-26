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
@Table(name="role")
public class Role implements Serializable {

	private static final long serialVersionUID = -5741530610160707366L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(length=30)
	private String name;
}