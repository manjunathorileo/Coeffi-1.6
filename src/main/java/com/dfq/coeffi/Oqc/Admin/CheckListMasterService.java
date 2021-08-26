package com.dfq.coeffi.Oqc.Admin;

import java.util.List;

public interface CheckListMasterService {

    CheckListMaster createCheckListMaster(CheckListMaster checkListMaster);
    List<CheckListMaster> getAllCheckListMaster();
    CheckListMaster getCheckListMaster(long id);
}
