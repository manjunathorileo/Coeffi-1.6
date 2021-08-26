package com.dfq.coeffi.StoreManagement.Entity;

import com.dfq.coeffi.Expenses.Entities.ExpensesEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class EmployeeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String requestNumber;
    private String department;
    private String costCenter;
    private String purpose;
    @JsonFormat(pattern="yyyy-mm-dd hh:mm:ss")
    private Date date;
    private String requestedBy;
    private String approvedBy;
    @JsonFormat(pattern="yyyy-mm-dd hh:mm:ss")
    private Date approvedDate;
    private long employeeId;
    private long managerId;
    private String otp;
    private String employeeName;
    private String purchaseType;
    private String customerName;
    private long jobOrderNumber;
    private String productName;
    private String materialType;
    private String reasonCode;
    private boolean requestIndication;
    private boolean qualityIndication;
    @JsonFormat(pattern="yyyy-mm-dd hh:mm:ss")
    private Date rejectDate;
    @JsonFormat(pattern="yyyy-mm-dd hh:mm:ss")
    private Date issuedDate;
    @Enumerated(EnumType.STRING)
    private MaterialsEnum  MaterialsStatus;


    private String  approvalEnum;

    @Enumerated(EnumType.STRING)
    private OtpValidationEnum otpValidation;

    @Temporal(TemporalType.DATE)
    private Date markedOn;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Materials> materials;
}
