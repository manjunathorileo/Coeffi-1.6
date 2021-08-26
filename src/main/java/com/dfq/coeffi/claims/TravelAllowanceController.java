package com.dfq.coeffi.claims;

import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class TravelAllowanceController extends BaseController {

    private TravelAllowanceService travelAllowanceService;

    @Autowired
    public TravelAllowanceController(TravelAllowanceService travelAllowanceService){
        this.travelAllowanceService = travelAllowanceService;
    }

    @PostMapping("travelling-allowance/create")
    public ResponseEntity<TravelAllowance> createTADA(@Valid @RequestBody TravelAllowance travelAllowance){
    travelAllowance.setTravellingApprovalStatus(TravellingApprovalStatus.APPLICATION_CREATED);
    TravelAllowance saveTADA=travelAllowanceService.applyTravellingAllowanceDearnessAllowance(travelAllowance);
    return new ResponseEntity<>(saveTADA,HttpStatus.OK);
    }

    @GetMapping("travelling-allowance/get-all")
    public ResponseEntity<List<TravelAllowance>> getAllTADA(){
    List<TravelAllowance> allTADA=travelAllowanceService.getAllTADA();
    return new ResponseEntity<>(allTADA,HttpStatus.OK);
    }
}
