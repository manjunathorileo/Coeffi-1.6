package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Bay;

import java.util.List;

public interface BayService {
    Bay saveBay(Bay bay);

    List<Bay> getBays();

    Bay getBay(long id);

    List<Bay> getByBayNumber(String bayNumber);

    Bay getBayByType(String bayNumber, String bayVehicleType);

    List<Bay> getByType(String typeOfVehicle);
}
