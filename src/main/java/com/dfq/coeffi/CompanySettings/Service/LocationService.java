package com.dfq.coeffi.CompanySettings.Service;

import com.dfq.coeffi.CompanySettings.Entity.Location;

import java.util.List;

public interface LocationService {
    Location saveLocation(Location location);
    List<Location>  getLocation();
    Location getLocationById(long id);
    void delete(long id);
}
