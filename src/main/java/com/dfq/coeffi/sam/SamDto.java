package com.dfq.coeffi.sam;

import com.dfq.coeffi.sam.module.Module;
import com.dfq.coeffi.sam.privileges.Privileges;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class SamDto {

    private long empId;
    private List<Module> modules;
    private List<Privileges> privileges;
}