package com.dfq.coeffi.GeoLocation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoLocationDto {
    private long empId;
    private String latitude;
    private String longitude;
    private String place;
    private long entryBodyTemperature;
    private long exitBodyTemperature;
    private boolean maskWearing;
    private String entryGateNumber;
    private String exitGateNumber;
}