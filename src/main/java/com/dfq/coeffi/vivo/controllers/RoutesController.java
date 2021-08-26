package com.dfq.coeffi.vivo.controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.vivo.entity.Routes;
import com.dfq.coeffi.vivo.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class RoutesController extends BaseController
{
   @Autowired
   RoutesService routesService;
    @PostMapping("routes")
    public ResponseEntity<List<Routes>> saveRoutes(@RequestBody List<Routes> routesList)
    {
        for(Routes routes:routesList) {
            routes.setStatus(true);
            Routes routes1 = routesService.saveRoutes(routes);
        }
        if(routesList.isEmpty()){
            throw new EntityNotFoundException("No Routes");
        }
        return new ResponseEntity<>(routesList, HttpStatus.OK);
    }
    @GetMapping("routes")
    public ResponseEntity<List<Routes>> getAllRoutes()
    {
        List<Routes> routes2=routesService.getAllRoutes();
        List<Routes> routesList=new ArrayList<>();
        for(Routes r:routes2){
            if(r.isStatus()){
                routesList.add(r);
            }
        }
        return new ResponseEntity<>(routesList,HttpStatus.OK);
    }
    @GetMapping("route/{id}")
    public ResponseEntity<Optional<Routes>> getRoutesById(@PathVariable long id)
    {
        Optional<Routes> routes3=routesService.getRoutesById(id);
        if(routes3.get().isStatus()){
            return new ResponseEntity<>(routes3,HttpStatus.OK);
        }
        else
            throw new EntityNotFoundException("No active routes");

    }

    @GetMapping("route-delete/{id}")
    public void deleteVisitorsByid(@PathVariable long id)
    {
        Optional<Routes> r=routesService.getRoutesById(id);
        r.get().setStatus(false);
        routesService.saveRoutes(r.get());
    }

}
