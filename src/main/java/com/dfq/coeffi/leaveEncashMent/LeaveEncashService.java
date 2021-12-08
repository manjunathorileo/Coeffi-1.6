package com.dfq.coeffi.leaveEncashMent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveEncashService {

    @Autowired
    LeaveEncashRepository leaveEncashRepository;

    public void save(LeaveEncash leaveEncash){
        leaveEncashRepository.save(leaveEncash);
    }

    public List<LeaveEncash> getAll(){
        List<LeaveEncash> leaveEncashes = leaveEncashRepository.findAll();
        return leaveEncashes;
    }


}
