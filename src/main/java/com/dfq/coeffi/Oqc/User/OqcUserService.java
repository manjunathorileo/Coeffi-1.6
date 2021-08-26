package com.dfq.coeffi.Oqc.User;

import java.util.List;

public interface OqcUserService {

    OqcUser createOqcUser(OqcUser oqcUser);
    List<OqcUser> getAllOqcUser();
    OqcUser getOqcUser(long id);
    List<OqcUser> getOqcUserByProductByProductionLine(long productId, long productionLineId);
}
