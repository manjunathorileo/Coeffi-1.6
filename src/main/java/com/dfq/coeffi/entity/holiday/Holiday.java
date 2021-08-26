package com.dfq.coeffi.entity.holiday;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.dialect.MySQLDialect;

@Getter
@Setter
@Entity
@Table(name="holiday")
public class Holiday implements Serializable {
	private static final long serialVersionUID = 3676850416754262106L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private Date startDate;
	
	@Column
	private Date endDate;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern ="yyyy-MM-dd")
	private Date createdOn;

	private String holidayType;

	private String holidayName;
	
	@OneToOne
	private AcademicYear academicYear;


}
