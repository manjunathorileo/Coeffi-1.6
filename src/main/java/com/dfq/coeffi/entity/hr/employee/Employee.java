package com.dfq.coeffi.entity.hr.employee;
/*
 * @author Ashvini B
 */

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.DepartmentTracker;
import com.dfq.coeffi.entity.hr.Designation;
import com.dfq.coeffi.entity.hr.ProbationaryPeriodStatus;
import com.dfq.coeffi.entity.payroll.EmployeeCTCData;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.i18n.Language;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.sam.module.Module;
import com.dfq.coeffi.sam.privileges.Privileges;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name="employee")
public class Employee implements Serializable{

	private static final long serialVersionUID = -3781887609609178736L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length=25)
	private String employeeCode;

	private String firstName;

	private String middleName;

	private String lastName;

	private boolean epf;
	@Column
	private Date dateOfJoining;

	@Column
	private Date dateOfLeaving;

	@Column(length=50)
	private String jobTitle;

	@Column
	private Date dateOfBirth;

	@Column(length=4)
	private String age;

	@Column(length=40)
	private String bloodGroup;

	@Column(length=10)
	private String gender;

	private String religion;

	private String caste;

	private String subCaste;

	@Column(length=210)
	private String maritalStatus;

	@Column(length = 600)
	private String imagePath;

	private String fatherName;

	private String motherName;

	private String wifeName;

	@Column(length=50,unique=true)
	private String adharNumber;

	@Column(length=15)
	private String phoneNumber;

	@Column(length=15)
	private String emergencyPhoneNumber;

	@Column(length=25)
	private String esiNumber;

	@Column(length=10)
	private double offeredSalary;

	@Column(length=50)
	private String refName;

	@Column(length=10)
	private Integer refNumber;

	@Column(length=50)
	private String pfNumber;
/* Added variables*/
	@Column
	@Temporal(TemporalType.DATE)
	private Date dateOfConfirmed;

	@Column(length=10)
	private Integer familyDependents;

	@Column(length=10)
	private String level;

	@Column(length=10)
	private String probationaryPeriod;

	@Column(length=25)
	private String uanNumber;

	@Column(length=10)
	private String panNumber;

	private String permanentAddress;

	private String currentAddress;

	@Column
	private Date dateOfMarriage;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
	private List<FamilyMember> familyMember;

	@Column
	private Boolean status;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "employee")
	private List<Qualification> qualification;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
	private List<PreviousEmployement> previousEmployement;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
	private List<EmployeeCertification> employeeCertifications;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
	private List<EmployeeAddress> employeeAddress;

	@OneToOne
	private User employeeLogin;

	@OneToOne(cascade = CascadeType.ALL)
	private EmployeeBank employeeBank;

//	@OneToOne(cascade = CascadeType.ALL)
	@OneToOne
	private Department department;

	@OneToOne(cascade = CascadeType.ALL)
	private Designation designation;

	@Enumerated(EnumType.STRING)
	private EmployeeType employeeType;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Module> modules;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Privileges> privileges;

	@OneToOne(cascade = CascadeType.ALL)
	private EmployeeCTCData employeeCTCData;

	@OneToOne(cascade = CascadeType.ALL)
	private Employee firstApprovalManager;

	@OneToOne(cascade = CascadeType.ALL)
	private Employee secondApprovalManager;

	@OneToOne(cascade = CascadeType.ALL)
	private Employee thirdApprovalManager;

	@OneToOne(cascade = CascadeType.ALL)
	private Employee fourthApprovalManager;

	@OneToOne(cascade = CascadeType.ALL)
	private Employee fifthApprovalManager;

	@OneToOne(cascade = CascadeType.ALL)
	private Document familyPicDocument;

	@OneToOne(cascade = CascadeType.ALL)
	private Document profilePicDocument;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Document> documents;

	@OneToOne(cascade = CascadeType.ALL)
	private Document form16Document;

	private String referenceId;

	public Employee(){
		//default
	}

	@Enumerated(EnumType.STRING)
	private ProbationaryPeriodStatus probationaryPeriodStatus;

	@Enumerated(EnumType.STRING)
	private EmployeeType permanentType;

	private Date lastAppraisalDate;

	private BigDecimal totalNoticePeriod;
	@OneToOne
	public Language language;

	private String company;

	private String location;

	//-----For Contract Employee--------
	private String policeVerification;

	private String identificationMarks;

	private boolean safetyVest;

	private String safetyVestColour;

	private String contractCompany;

	private String role;

	private String reportingManager;

	private String departmentName;

	private String vehicleDetails;

	private long profilePicId;

	private long familyPicId;

	private long uploadFor;

	private long siteId;

	private boolean otRequired;

	private Date departmentAssignedOn;

	private Date designationStartDate;

	private Date designationEndDate;

	@OneToMany
	private List<DepartmentTracker> departmentTrackerList;

	private int firstWeekOff;

	private int secondWeekOff;

	private String firstWeekOffName;

	private String secondWeekOffName;

	private String rfid;

	private boolean flexi;
	private double mins;
	private boolean releaved;
}