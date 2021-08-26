package com.dfq.coeffi.archiveData;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class AttendanceRawDataController extends BaseController {

    @Autowired
    AttendanceRawDataRepository attendanceRawDataRepository;
    @Autowired
    ArchiveAttendanceRepository archiveAttendanceRepository;

    /**
     * Store raw data for one month and Archive later
     */
    @GetMapping("archive-one-month-data")
    public void archiveRawData() {
        List<AttendanceRawData> attendanceRawDataList = attendanceRawDataRepository.findAll();
        for (AttendanceRawData attendanceRawData : attendanceRawDataList) {
            long days = DateUtil.getDifferenceDays(attendanceRawData.getMarkedOn(), new Date());
            if (days > 90) {
                ArchiveAttendanceData archiveAttendanceData = new ArchiveAttendanceData();
                archiveAttendanceData.setTransactionDate(new Date());
                archiveAttendanceData.setMarkedOn(attendanceRawData.getMarkedOn());
                if (attendanceRawData.getDirection() != null) {
                    archiveAttendanceData.setDirection(attendanceRawData.getDirection());
                }
                archiveAttendanceData.setEmployeeCode(attendanceRawData.getEmployeeCode());
                archiveAttendanceData.setLogDate(attendanceRawData.getLogDate());
                archiveAttendanceData.setDeviceLogId(archiveAttendanceData.getDeviceLogId());
                archiveAttendanceRepository.save(archiveAttendanceData);
                attendanceRawDataRepository.delete(attendanceRawData);
            }
        }
    }

    /**
     * Clear archived data after 6 months
     */
    @GetMapping("clear-archive")
    public void clearArchive() {
        List<ArchiveAttendanceData> archiveAttendanceDataList = archiveAttendanceRepository.findAll();
        for (ArchiveAttendanceData archiveAttendanceData : archiveAttendanceDataList) {
            long days = DateUtil.getDifferenceDays(archiveAttendanceData.getTransactionDate(), new Date());
            if (days > (30 * 6)) {
                archiveAttendanceRepository.delete(archiveAttendanceData);
            }
        }
    }
}
