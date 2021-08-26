package com.dfq.coeffi.auditlog.log;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class ApplicationLogController extends BaseController {
    @Autowired
    private final ApplicationLogService applicationLogService;

    @Autowired
    public ApplicationLogController(ApplicationLogService applicationLogService) {
        this.applicationLogService = applicationLogService;
    }

    @GetMapping("log")
    public ResponseEntity<List<ApplicationLog>> listOfLogs() {
        List<ApplicationLog> logs = applicationLogService.listAllApplicationLogs();
        if (CollectionUtils.isEmpty(logs)) {
            throw new EntityNotFoundException("logs");
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @GetMapping("log/today")
    public ResponseEntity<List<ApplicationLog>> listOfTodayLogs() {
        List<ApplicationLog> logs = applicationLogService.getApplicationLogsByDate(DateUtil.getTodayDate());
        if (CollectionUtils.isEmpty(logs)) {
            throw new EntityNotFoundException("logs");
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @GetMapping("log/user")
    public ResponseEntity<List<ApplicationLog>> listOfUserLogs() {
        List<ApplicationLog> logs = applicationLogService.getApplicationLogsByDate(DateUtil.getTodayDate());
        if (CollectionUtils.isEmpty(logs)) {
            throw new EntityNotFoundException("logs");
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}