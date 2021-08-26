package com.dfq.coeffi.visitor.Entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


@Setter
@Getter
public class VisitorDateDto {

        @Temporal(TemporalType.DATE)
        @DateTimeFormat(pattern="yyyy-MM-dd")
        public Date startDate;

        @Temporal(TemporalType.DATE)
        @DateTimeFormat(pattern="yyyy-MM-dd")
        public Date endDate;

        private String departmentName;
        private String visitType;
        private String mobileNumber;
        private String firstName;
        private String lastNme;
        private Date dateOfVisit;
        private String companyName;
}
