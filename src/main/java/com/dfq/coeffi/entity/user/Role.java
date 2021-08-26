package com.dfq.coeffi.entity.user;
/**
 * @author H Kapil Kumar
 *
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="role")
public class Role {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column
	private long id;

	@Column(length = 45)
	private String name;

	@Column
	private Boolean status;

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "created_on", updatable = false)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@Column(name = "modified_on")
	private Date modifiedOn;

	@JsonIgnore
	@OneToOne
	private User createdBy;

	Role(){}

	public Role(long id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
	}
}
