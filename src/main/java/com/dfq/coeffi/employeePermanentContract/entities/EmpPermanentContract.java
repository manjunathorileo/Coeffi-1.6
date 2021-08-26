package com.dfq.coeffi.employeePermanentContract.entities;

import com.dfq.coeffi.employeePermanentContract.controllers.SitePcController;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmpPermanentContract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 25)
    private String employeeCode;

    private String firstName;

    private String middleName;

    private String lastName;

    private EmployeeType permanentType;

    private boolean epf;

    @Column
    private Date dateOfJoining;

    @Column
    private Date dateOfLeaving;

    @Column(length = 50)
    private String jobTitle;

    @Column
    private Date dateOfBirth;

    @Column(length = 4)
    private String age;

    @Column(length = 40)
    private String bloodGroup;

    @Column(length = 50, unique = true)
    private String adharNumber;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 15)
    private String emergencyPhoneNumber;

    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;

    private boolean valid;

    private boolean status;


    @Column(length = 10)
    private String gender;

    private String religion;

    private String caste;

    private String subCaste;

    @Column(length = 210)
    private String maritalStatus;

    @Column(length = 600)
    private String imagePath;

    private String fatherName;

    private String motherName;

    private String wifeName;

    @Column(length = 10)
    private String panNumber;

    private String permanentAddress;

    private String currentAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private Document familyPicDocument;

    @OneToOne(cascade = CascadeType.ALL)
    private Document profilePicDocument;

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

    private long inTime;

    private long outTime;

    private Date startDate;

    private Date endDate;

    @CreationTimestamp
    private Date createdOn;

    private long siteId;

    private String accessId;

    private String email;

    @OneToOne
    PermanentContractAttendance permanentContractAttendance;

    private String cardId;

    private String location;

    @OneToOne
    private Department department;

    private boolean belongsTo;

    private boolean isTemporaryContract;

    private boolean releaved;

    private String expiryStatus;





}
