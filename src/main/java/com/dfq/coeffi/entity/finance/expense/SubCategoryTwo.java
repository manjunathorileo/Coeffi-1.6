package com.dfq.coeffi.entity.finance.expense;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Auther H Kapil Kumar on 19/3/18.
 * @Company Orileo Technologies
 */

@Entity
@Setter
@Getter
@ToString
public class SubCategoryTwo implements Serializable
{
	private static final long serialVersionUID = 7523814095442212712L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @OneToOne
    private SubCategoryOne subCategoryOne;
}