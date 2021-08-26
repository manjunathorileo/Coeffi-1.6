package com.dfq.coeffi.auditlog.log;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ApplicationLogService {
	List<ApplicationLog> listAllApplicationLogs();
	Optional<ApplicationLog> getApplicationLog(long id);
    List<ApplicationLog> getApplicationLogsByDate(Date date);
    List<ApplicationLog> getLoggedUserLogs(long loggedUserId);
    ApplicationLog recordApplicationLog(String operationDoneBy, String description, String operationType, long loggedUserId);
}
