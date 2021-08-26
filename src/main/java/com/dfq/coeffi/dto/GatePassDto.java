package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.leave.GatePassStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GatePassDto {
        public List<Long> gatePassIds;
        public GatePassStatus gatePassStatus;

        public Date inputDate;
        public long id;
        public String description;

        private String actualExit;
        private String actualEntry;




}
