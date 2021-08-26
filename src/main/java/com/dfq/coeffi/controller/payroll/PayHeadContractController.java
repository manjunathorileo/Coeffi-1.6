package com.dfq.coeffi.controller.payroll;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.payroll.payrollmaster.PayHead;
import com.dfq.coeffi.entity.payroll.payrollmaster.PayHeadContract;
import com.dfq.coeffi.service.payroll.PayHeadContractService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class PayHeadContractController extends BaseController {

    @Autowired
    private PayHeadContractService payHeadNonTeachingService;

    @Autowired
    public PayHeadContractController(PayHeadContractService payHeadNonTeachingService) {
        this.payHeadNonTeachingService = payHeadNonTeachingService;
    }

    /**
     * @return all the Employees PayHead Master List with details in the database
     */
    @GetMapping("payHead-Non-Teaching")
    public ResponseEntity<List<PayHeadContract>> getAllPayHeadService() {
        List<PayHeadContract> payheads = payHeadNonTeachingService.getAllPayHeadNonTeaching();
        System.out.println("================Printing No of Values in Payhead-Non Teaching=============\n" + payheads.size());
        if (CollectionUtils.isEmpty(payheads)) {
            throw new EntityNotFoundException("payheads");
        }


        return new ResponseEntity<>(payheads, HttpStatus.OK);
    }


    /**
     * @param payHeadContract : save object to database and return the saved object
     * @return
     */

    @PostMapping("payHead-Non-Teaching")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PayHeadContract> createPayHead(@Valid @RequestBody PayHeadContract payHeadContract) {
        PayHeadContract persistedObject = payHeadNonTeachingService.createPayHeadNonTeaching(payHeadContract);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    /**
     * @param id
     * @param payHeadContract
     * @return to update the payHead object
     */

    @PutMapping("payHead-Non-Teaching/{id}")
    public ResponseEntity<PayHeadContract> update(@PathVariable long id, @Valid @RequestBody PayHeadContract payHeadContract) {
        Optional<PayHeadContract> persistedpayHead = payHeadNonTeachingService.getPayHeadNonTeaching(id);
        if (!persistedpayHead.isPresent()) {
            throw new EntityNotFoundException(PayHead.class.getSimpleName());
        }
        payHeadContract.setId(id);
        payHeadNonTeachingService.createPayHeadNonTeaching(payHeadContract);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id
     * @return permanent deactivate of PayHead by provided id | no permanent delete
     */

    @DeleteMapping("payHead-Non-Teaching/{id}")
    public ResponseEntity<PayHeadContract> deletePayHead(@PathVariable long id) {
        Optional<PayHeadContract> payHead = payHeadNonTeachingService.getPayHeadNonTeaching(id);
        if (!payHead.isPresent()) {
            throw new EntityNotFoundException(PayHead.class.getName());
        }
        payHeadNonTeachingService.deletePayHeadNonTeaching(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
