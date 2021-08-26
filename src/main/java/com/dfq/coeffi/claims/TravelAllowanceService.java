package com.dfq.coeffi.claims;

import java.util.List;
import java.util.Optional;

public interface TravelAllowanceService {
    TravelAllowance applyTravellingAllowanceDearnessAllowance(TravelAllowance TravelAllowance);
    List<TravelAllowance> getAllTADA();
    List<TravelAllowance> getTADAByStatus(TravellingApprovalStatus travellingApprovalStatus);
    Optional<TravelAllowance> getTADAById(long id);
}
