package com.dfq.coeffi.entity.library;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/*
 * @author Azhar razvi
 */

@Getter
@Setter
@Entity
@Table(name="category_book")
public class BookCategory implements Serializable 
{

	private static final long serialVersionUID = -7557181469297798158L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private Boolean status=true;
	
}
