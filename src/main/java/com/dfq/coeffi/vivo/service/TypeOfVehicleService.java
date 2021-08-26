package com.dfq.coeffi.vivo.service;


import com.dfq.coeffi.vivo.entity.TypeOfVehicle;

import java.util.List;
import java.util.Optional;

public interface TypeOfVehicleService
{
    TypeOfVehicle saveVehicle(TypeOfVehicle typeOfVehicle);

    List<TypeOfVehicle> getAllVehicles();

    Optional<TypeOfVehicle> getVehicleById(long id);

    void deleteVehicleByid(long id);
}
