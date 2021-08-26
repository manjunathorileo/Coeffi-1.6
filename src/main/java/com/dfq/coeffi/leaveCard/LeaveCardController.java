package com.dfq.coeffi.leaveCard;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class LeaveCardController extends BaseController {

    @Autowired
    LeaveCardService leaveCardService;
    @Autowired
    PermanentContractService permanentContractService;

    @PostMapping("leave-card/{empid}/{month}/{year}/{id}")
    public ResponseEntity<LeaveCard> create(@RequestParam("file") MultipartFile file, @PathVariable long empid, @PathVariable String month, @PathVariable String year, @PathVariable long id) throws IOException {
        EmpPermanentContract empPermanentContract = permanentContractService.get(empid);
        if (id > 0) {
            LeaveCard leaveCard1 = leaveCardService.get(id);
            leaveCard1.setData(file.getBytes());
            leaveCard1.setEmpPermanentContract(empPermanentContract);
            leaveCard1.setEmpId(empid);
            leaveCard1.setYear(year);
            leaveCard1.setFileType(file.getContentType());
            leaveCard1.setMonth(month);
            leaveCardService.save(leaveCard1);
        } else {
            LeaveCard leaveCard1 = new LeaveCard();
            leaveCard1.setData(file.getBytes());
            leaveCard1.setEmpPermanentContract(empPermanentContract);
            leaveCard1.setEmpId(empid);
            leaveCard1.setYear(year);
            leaveCard1.setMonth(month);
            leaveCard1.setFileType(file.getContentType());
            leaveCardService.save(leaveCard1);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("leave-cards")
    public ResponseEntity<List<LeaveCard>> getMonthlyView(@RequestBody LeaveCard leaveCard) {
        List<LeaveCard> leaveCards = leaveCardService.get(leaveCard.getMonth(), leaveCard.getYear());
        List<LeaveCard> leaveCardList = new ArrayList<>();
        for (LeaveCard l : leaveCards) {
            l.getEmpPermanentContract().setProfilePicDocument(null);
            l.setData(null);
            if (l.getEmpPermanentContract().getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                leaveCardList.add(l);
            }
        }
        return new ResponseEntity<>(leaveCardList, HttpStatus.OK);
    }

    @PostMapping("leave-cards-contract")
    public ResponseEntity<List<LeaveCard>> getMonthlyViewContract(@RequestBody LeaveCard leaveCard) {
        List<LeaveCard> leaveCards = leaveCardService.get(leaveCard.getMonth(), leaveCard.getYear());
        List<LeaveCard> leaveCardList = new ArrayList<>();
        for (LeaveCard l : leaveCards) {
            l.getEmpPermanentContract().setProfilePicDocument(null);
            l.setData(null);
            if (l.getEmpPermanentContract().getEmployeeType().equals(EmployeeType.CONTRACT)) {
                leaveCardList.add(l);
            }
        }
        return new ResponseEntity<>(leaveCardList, HttpStatus.OK);
    }

    @GetMapping("leave-card/{id}")
    public ResponseEntity<LeaveCard> getCard(@PathVariable long id) {
        LeaveCard leaveCard = leaveCardService.get(id);
        return new ResponseEntity<>(leaveCard, HttpStatus.OK);
    }


    @DeleteMapping("leave-card/{id}")
    public void deleteLeaveCard(@PathVariable long id) {
        leaveCardService.delete(id);
    }

    @GetMapping("/download-leave-card/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long id) {
        // Load file from database
        LeaveCard leaveCard = leaveCardService.get(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(leaveCard.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "leave_card_" + leaveCard.getEmpPermanentContract().getEmployeeCode() + "\"")
                .body(new ByteArrayResource(leaveCard.getData()));
    }
}
