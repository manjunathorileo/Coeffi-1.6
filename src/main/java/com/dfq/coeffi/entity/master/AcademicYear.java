package com.dfq.coeffi.entity.master;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @Auther H Kapil Kumar on 19/3/18.
 * @Company Orileo Technologies
 */

@Setter
@Getter
@Entity
@ToString
public class AcademicYear
{
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
	@Column(name="academic_range")
    private String range;
	
	@Column
	private boolean status;

    private String year;
}