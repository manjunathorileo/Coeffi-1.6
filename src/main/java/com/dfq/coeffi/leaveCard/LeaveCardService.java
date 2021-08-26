package com.dfq.coeffi.leaveCard;

import java.util.List;

public interface LeaveCardService {
    LeaveCard save(LeaveCard leaveCard);

    LeaveCard get(long id);

    List<LeaveCard> getAll();

    List<LeaveCard> get(String month, String year);

    void delete(long id);

}
