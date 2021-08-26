package com.dfq.coeffi.vivo.service;


import com.dfq.coeffi.vivo.entity.Routes;

import java.util.List;
import java.util.Optional;

public interface RoutesService
{
    Routes saveRoutes(Routes routes);

    List<Routes> getAllRoutes();

    Optional<Routes> getRoutesById(long id);

    void deleteRoutesByid(long id);
}
