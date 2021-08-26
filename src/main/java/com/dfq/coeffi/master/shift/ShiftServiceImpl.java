package com.dfq.coeffi.master.shift;


import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Override
    public Shift createNewShift(Shift shift) {
        shift.setStatus(true);
        return shiftRepository.save(shift);
    }

    @Override
    public List<Shift> findByStatus(boolean status) {
        return shiftRepository.findByStatus(status);
    }

    @Override
    public Shift getShift(long id) {
        return shiftRepository.findOne(id);
    }

    @Override
    public Shift getShiftByName(String shiftName) {
        return shiftRepository.findByName(shiftName);
    }

    @Override
    public void deleteShift(long id) {
        shiftRepository.deactivate(id);
    }

    public Shift getCurrentShiftWithGrace() {
        int currentTime = DateUtil.getRunningHour();
        System.out.println("CurrentTime " + currentTime);
        List<Shift> shiftList = new ArrayList<>();
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
            for (Shift runningShift : shifts) {
                if (currentTime+runningShift.getShiftGraceBefore()>= DateUtil.getRunningHour(runningShift.getStartTime()) && currentTime+runningShift.getShiftGraceAfter() <= DateUtil.getRunningHour(runningShift.getEndTime())) {
                    shiftList.add(runningShift);
                }
            }

        }
        if (shiftList.isEmpty()){
            return null;
        }else {
            return shiftList.get(0);
        }
    }

}