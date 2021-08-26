package com.dfq.coeffi.entity.payroll;

import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.master.shift.Shift;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
public class EmployeeAttendance implements Serializable {

	private static final long serialVersionUID = 6331902193138497864L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Temporal(TemporalType.DATE)
	private Date markedOn;

	@Enumerated(EnumType.STRING)
	@Column(length = 45)
	private AttendanceStatus attendanceStatus;

	@OneToOne
	private Employee employee;

	private Date recordedTime;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
	private Date inTime;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
	private Date outTime;

	private String workedHours;

	private String lateEntry;

	private String overTime;

	private String effectiveOverTime;

	private String earlyOut;

	@OneToOne
	private Shift shift;

	private String latitudeIn;

	private String latitudeOut;

	private String longitudeIn;

	private String longitudeOut;

	private String placeIn;

	private String placeOut;

	private String leaveHalfType;

	private String otF;

	private String eOtF;

	private String whF;

	private String lateEntryNotFormated;

	private String earlyOutNotFormated;

	private boolean isWeekOffPresent;

	private Date startDate;

	private Date endDate;

	private boolean dataProcessed;
}
