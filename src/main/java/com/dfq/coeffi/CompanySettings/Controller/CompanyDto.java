package com.dfq.coeffi.CompanySettings.Controller;

import com.dfq.coeffi.CompanySettings.Entity.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyDto {
    private long companyId;
    private List<Location> locations;
}
