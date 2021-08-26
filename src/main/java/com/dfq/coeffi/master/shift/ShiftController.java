package com.dfq.coeffi.master.shift;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class ShiftController extends BaseController {

    @Autowired
    private ShiftService shiftService;
    @Autowired
    private ShiftRepository shiftRepository;


    @PostMapping("/shift")
    public ResponseEntity<Shift> createShift(@RequestBody Shift shift) {
        Shift persistedShift = shiftService.createNewShift(shift);
        return new ResponseEntity<>(persistedShift, HttpStatus.OK);
    }

    @GetMapping("/shift")
    public ResponseEntity<List<Shift>> listOfShifts() {
        List<Shift> shifts = shiftService.findByStatus(true);
        return new ResponseEntity<List<Shift>>(shifts, HttpStatus.OK);
    }

    @PostMapping("/shift/{id}")
    public ResponseEntity<Shift> updateShift(@PathVariable long id, @RequestBody Shift shift) {
        Shift updateShift = shiftService.getShift(id);
        if (updateShift == null) {
            throw new EntityNotFoundException("Shift not found id: " + id);
        }
        shift.setId(id);
        shiftService.createNewShift(shift);
        return new ResponseEntity(shift, HttpStatus.OK);
    }

    @DeleteMapping("/shift/{id}")
    public ResponseEntity<Shift> deleteShift(@PathVariable long id) {
        Shift updateShift = shiftService.getShift(id);
        if (updateShift == null) {
            throw new EntityNotFoundException("Shift not found id: " + id);
        }
        shiftService.deleteShift(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/current-shift")
    public ResponseEntity<Shift> getCurrentShift() {
        System.out.println("hr::::::::::::::"+DateUtil.getRunningHour());
        int currentTime = DateUtil.getRunningHour();
        System.out.println("CurrentTime " + currentTime);
        List<Shift> shiftList = new ArrayList<>();
        Shift shift = null;
        List<Shift> shifts = null;
        if (shifts == null) {
            shifts = shiftRepository.findAll();
        }
        for (Shift runningShift : shifts) {
            if (currentTime >= DateUtil.getRunningHour(runningShift.getStartTime()) && currentTime <= DateUtil.getRunningHour(runningShift.getEndTime())) {
                shiftList.add(runningShift);
            }
        }
        if (shiftList.isEmpty()) {
            return null;
        } else {
            return new ResponseEntity<>(shiftList.get(0), HttpStatus.OK);
        }
    }

    @GetMapping("SHIFT-Time")
    public long getDifferenceMinutes(Date d1, Date d2) {
        Shift shift = shiftService.getShift(1);
        long diffMs = shift.getEndTime().getTime()-shift.getStartTime().getTime();
        long diffSec = diffMs / 1000;
        long min = diffSec / 60;
        return min;
    }
}
