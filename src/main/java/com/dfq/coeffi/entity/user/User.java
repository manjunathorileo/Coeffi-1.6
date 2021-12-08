/**
 * 
 */
package com.dfq.coeffi.entity.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author H Kapil Kumar
 *
 */
@Setter
@Getter
@Entity
@Table(name="user")
public class User 
{
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String firstName;
	private String lastName;
	private String password;
	private boolean active;
	private String email;

	@Column
	private Date licenceStartDate;

	@Column
	private Date licenceEndDate;

	@Column
	private Boolean isEligible;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Role> roles;

	private long empId;

	public User(){}
	
	public User(User user) {
		this.active = user.active;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.roles = user.roles;
		this.email = user.email;
		this.password = user.password;
		this.id = user.id;
	}
}