package com.dfq.coeffi.evacuationApi;

import com.dfq.coeffi.controller.BaseController;
import javafx.print.Collation;
import lombok.extern.slf4j.Slf4j;
import org.jboss.vfs.util.LazyInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class EvacuationTrackerController extends BaseController {
    @Autowired
    EvacuationTrackerRepository evacuationTrackerRepository;

    @GetMapping("evacuation-tracker")
    public ResponseEntity<EvacuationTracker> evacuationTracker() {
        List<EvacuationTracker> evacuationTrackers = evacuationTrackerRepository.findAll();
        Collections.reverse(evacuationTrackers);
        EvacuationTracker evacuationTracker = new EvacuationTracker();
        if (!evacuationTrackers.isEmpty()) {
            evacuationTracker = evacuationTrackers.get(0);
        }
        return new ResponseEntity<>(evacuationTracker, HttpStatus.OK);
    }
}
