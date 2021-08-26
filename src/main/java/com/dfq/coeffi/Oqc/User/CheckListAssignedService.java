package com.dfq.coeffi.Oqc.User;

import java.util.List;

public interface CheckListAssignedService {

    CheckListAssigned createCheckListAssigned(CheckListAssigned checkListAssigned);
    List<CheckListAssigned> getAllCheckListAssigned();
    CheckListAssigned getCheckListAssigned(long id);
}
