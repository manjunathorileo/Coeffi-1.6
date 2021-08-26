package com.dfq.coeffi.entity.finance.expense;
/**
 * @Auther H Kapil Kumar on 19/3/18.
 * @Company Orileo Technologies
 */

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Setter
@Getter
@ToString
public class SubCategoryOne implements Serializable
{
	private static final long serialVersionUID = 7620613126223446464L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @OneToOne
    private SubCategory subCategory;
}