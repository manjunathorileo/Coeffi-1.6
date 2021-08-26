package com.dfq.coeffi.master.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Setter
@Getter
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date startTime;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date endTime;

    private boolean status;

    private long graceInTime;

    private long graceOutTme;

    private long shiftGraceBefore;

    private long shiftGraceAfter;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date lunchStartTime;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date lunchEndTime;

    private long lunchDownTime;
}