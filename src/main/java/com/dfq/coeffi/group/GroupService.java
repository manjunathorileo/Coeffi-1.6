package com.dfq.coeffi.group;

import java.util.List;

public interface GroupService {

    List<Group> listAllGroups();
    Group getGroup(long id);
    Group createNewGroup(Group group);
    void deactivateGroup(Long id);

}