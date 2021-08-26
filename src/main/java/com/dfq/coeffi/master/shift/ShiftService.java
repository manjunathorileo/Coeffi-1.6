package com.dfq.coeffi.master.shift;


import java.util.List;


public interface ShiftService {
    Shift createNewShift(Shift shift);
    List<Shift> findByStatus(boolean status);
    Shift getShift(long id);
    Shift getShiftByName(String shiftName);
    void deleteShift(long id);
    public Shift getCurrentShiftWithGrace();
}