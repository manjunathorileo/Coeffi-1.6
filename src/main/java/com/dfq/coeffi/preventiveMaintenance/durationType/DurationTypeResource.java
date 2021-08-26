package com.dfq.coeffi.preventiveMaintenance.durationType;

import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class DurationTypeResource extends BaseController {

    private final DurationTypeService durationTypeService;

    @Autowired
    public DurationTypeResource(DurationTypeService durationTypeService) {
        this.durationTypeService = durationTypeService;
    }

    @PostMapping("/duration-type")
    public ResponseEntity<DurationType> saveDurationType(@RequestBody @Valid DurationType durationType){
        List<DurationType> durationTypes = durationTypeService.getAllDurationType();
        for (DurationType durationTypeObj:durationTypes) {
            if (durationTypeObj.getDurationType().equals(durationType.getDurationType())){
                throw new EntityNotFoundException("Already there is one duration with this duration name.");
            }
        }
        durationType.setStatus(true);
        DurationType durationTypeObj = durationTypeService.saveDurationType(durationType);
        return new ResponseEntity(durationTypeObj, HttpStatus.OK);
    }

    @GetMapping("/duration-type")
    public ResponseEntity<DurationType> getAllDurationType(){
        List<DurationType> durationTypes = durationTypeService.getAllDurationType();
        if (durationTypes.isEmpty()){
            throw new EntityNotFoundException("There is no duration type");
        }
        return new ResponseEntity(durationTypes, HttpStatus.OK);
    }

    @GetMapping("/duration-type/{id}")
    public ResponseEntity<DurationType> getDurationType(@PathVariable long id){
        Optional<DurationType> durationType = durationTypeService.getDurationById(id);
        return new ResponseEntity(durationType.get(), HttpStatus.OK);
    }

    @DeleteMapping("/duration-type/{id}")
    public ResponseEntity<DurationType> deleteDurationType(@PathVariable long id){
        DurationType durationType = durationTypeService.deleteDurationType(id);
        return new ResponseEntity(durationType, HttpStatus.OK);
    }
}
