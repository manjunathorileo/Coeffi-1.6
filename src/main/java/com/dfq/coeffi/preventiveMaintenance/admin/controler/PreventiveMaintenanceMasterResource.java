package com.dfq.coeffi.preventiveMaintenance.admin.controler;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.PreventiveMaintenanceMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.service.PreventiveMaintenanceMasterService;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class PreventiveMaintenanceMasterResource extends BaseController {

    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final DurationTypeService durationTypeService;
    private final PreventiveMaintenanceMasterService preventiveMaintenanceMasterService;

    @Autowired
    public PreventiveMaintenanceMasterResource(SopTypeService sopTypeService, SopCategoryService SOPCategoryService, DurationTypeService durationTypeService, PreventiveMaintenanceMasterService preventiveMaintenanceMasterService) {
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.durationTypeService = durationTypeService;
        this.preventiveMaintenanceMasterService = preventiveMaintenanceMasterService;
    }

    @GetMapping("/preventive-maintenance-master-list-by-sopType-SOPDetails-durationType-durationValue/{sopTypeId}/{digitalSopId}/{durationTypeId}/{durationValue}")
    public ResponseEntity<PreventiveMaintenanceMaster> getCheckPreventiveMaintenanceMaster(@PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long durationTypeId, @PathVariable long durationValue){
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(sopTypeId);
        Optional<SopCategory> SOPCategoryOptional = SOPCategoryService.getSopCategory(digitalSopId);
        SopCategory SOPCategory = SOPCategoryOptional.get();
        Optional<DurationType> durationType = durationTypeService.getDurationById(durationTypeId);
        PreventiveMaintenanceMaster preventiveMaintenanceMaster = preventiveMaintenanceMasterService.getPreventiveMaintenanceMasterByAssemblyLineByStages(sopTypeOptional.get(), SOPCategory, durationType.get(), durationValue);
        if (preventiveMaintenanceMaster == null){
            throw new EntityNotFoundException("There is no Check List for this asset.");
        }
        return new ResponseEntity<>(preventiveMaintenanceMaster, HttpStatus.OK);
    }

    @GetMapping("/preventive-maintenance-master-list/{id}")
    public ResponseEntity<PreventiveMaintenanceMaster> getPreventiveMaintenanceMaster(@PathVariable long id){
        Optional<PreventiveMaintenanceMaster> preventiveMaintenanceMaster = preventiveMaintenanceMasterService.getPreventiveMaintenanceMaster(id);
        return new ResponseEntity(preventiveMaintenanceMaster, HttpStatus.OK);
    }

    @GetMapping("/preventive-maintenance-master-list")
    public ResponseEntity<PreventiveMaintenanceMaster> getAllPreventiveMaintenanceMaster(){
        List<PreventiveMaintenanceMaster> preventiveMaintenanceMasters = preventiveMaintenanceMasterService.getAllPreventiveMaintenanceMaster();
        if (preventiveMaintenanceMasters.isEmpty()){
            throw new EntityNotFoundException("There is no data");
        }
        return new ResponseEntity(preventiveMaintenanceMasters, HttpStatus.OK);
    }
}
