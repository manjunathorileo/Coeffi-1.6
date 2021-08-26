package com.dfq.coeffi.entity.finance.expense;

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
@Entity(name="expense_category")
@ToString
public class Category
{
	private static final long serialVersionUID = -494727495674618328L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column
    private String name;

    @Column
    private boolean active;
}