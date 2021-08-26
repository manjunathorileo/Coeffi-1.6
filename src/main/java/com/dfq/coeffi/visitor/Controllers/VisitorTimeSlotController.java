package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.visitor.Entities.VisitorTimeSlot;
import com.dfq.coeffi.visitor.Services.VisitorTimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class VisitorTimeSlotController extends BaseController
{
    @Autowired
    private final VisitorTimeSlotService visitorTimeSlotService;

    public VisitorTimeSlotController(VisitorTimeSlotService visitorTimeSlotService) {
        this.visitorTimeSlotService = visitorTimeSlotService;
    }

    @PostMapping("visitor/timeslot-save")
    public ResponseEntity<VisitorTimeSlot> saveTime(@RequestBody VisitorTimeSlot  visitorTimeSlot)
    {
        VisitorTimeSlot visitorTimeSlot1= visitorTimeSlotService.saveTime(visitorTimeSlot) ;
        return new ResponseEntity<>(visitorTimeSlot1, HttpStatus.OK);
    }

    @GetMapping("visitor/timeslot-view")
    public ResponseEntity<List<VisitorTimeSlot>> getAllTime()
    {
        List<VisitorTimeSlot> visitorTimeSlot2= visitorTimeSlotService.getAllTime();
        return new ResponseEntity<>(visitorTimeSlot2,HttpStatus.OK);
    }

    @DeleteMapping("visitor/timeslot-delete/{id}")
    public void deleteTimeByid(@PathVariable long id)
    {
        visitorTimeSlotService.deleteTimeById(id);

    }

   

}
