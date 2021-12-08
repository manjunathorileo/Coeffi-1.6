package com.dfq.coeffi.LeaveSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LeaveSettingService {
    @Autowired
    LeaveSettingRepository leaveSettingRepository;

    public void save(LeaveSetting leaveSetting) {
        leaveSettingRepository.save(leaveSetting);
    }


    public LeaveSetting getLatest() {
        List<LeaveSetting> leaveSettings = leaveSettingRepository.findAll();
        Collections.reverse(leaveSettings);
        LeaveSetting leaveSetting = new LeaveSetting();
        if (!leaveSettings.isEmpty()) {
            leaveSetting = leaveSettings.get(0);
        }
        return leaveSetting;
    }
}
