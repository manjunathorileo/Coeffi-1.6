package com.dfq.coeffi.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public List<Group> listAllGroups() {
        return groupRepository.findGroupByStatus(true);
    }

    @Override
    public Group getGroup(long id) {
        return groupRepository.findOne(id);
    }

    @Override
    public Group createNewGroup(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public void deactivateGroup(Long id) {

    }
}