package com.dfq.coeffi.vivo.service;


import com.dfq.coeffi.vivo.entity.TypeOfVehicle;
import com.dfq.coeffi.vivo.repository.TypeOfVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeOfVehicleServiceImpl implements TypeOfVehicleService {
    @Autowired
    TypeOfVehicleRepository typeOfVehicleRepository;

    @Override
    public TypeOfVehicle saveVehicle(TypeOfVehicle typeOfVehicle) {
        return typeOfVehicleRepository.save(typeOfVehicle);
    }

    @Override
    public List<TypeOfVehicle> getAllVehicles() {
        return typeOfVehicleRepository.findAll();
    }

    @Override
    public Optional<TypeOfVehicle> getVehicleById(long id) {
        return typeOfVehicleRepository.findById(id);
    }

    @Override
    public void deleteVehicleByid(long id) {
        typeOfVehicleRepository.deleteById(id);
    }
}
