package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Routes;
import com.dfq.coeffi.vivo.repository.RoutesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoutesServiceImpl implements RoutesService
{
    @Autowired
    RoutesRepository routesRepository;

    @Override
    public Routes saveRoutes(Routes routes)
    {
        return routesRepository.save(routes);
    }

    @Override
    public List<Routes> getAllRoutes()
    {
        return routesRepository.findAll();
    }

    @Override
    public Optional<Routes> getRoutesById(long id)
    {
        return routesRepository.findById(id);
    }

    @Override
    public void deleteRoutesByid(long id)
    {
        routesRepository.deleteById(id);
    }
}
