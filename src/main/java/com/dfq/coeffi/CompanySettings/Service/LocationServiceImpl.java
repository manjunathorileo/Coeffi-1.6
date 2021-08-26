package com.dfq.coeffi.CompanySettings.Service;

import com.dfq.coeffi.CompanySettings.Entity.Location;
import com.dfq.coeffi.CompanySettings.Repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    LocationRepository locationRepository;

    @Override
    public Location saveLocation(Location location) {
        return locationRepository.save(location) ;
    }

    @Override
    public List<Location> getLocation() {
        List<Location> locationList=locationRepository.findAll();
        return locationList;
    }

    @Override
    public Location getLocationById(long id) {
        Location location=locationRepository.findOne(id);
        return location;
    }

    @Override
    public void delete(long id) {
        locationRepository.delete(id);

    }
}
